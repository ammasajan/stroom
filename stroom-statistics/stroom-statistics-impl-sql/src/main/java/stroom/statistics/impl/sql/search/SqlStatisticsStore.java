package stroom.statistics.impl.sql.search;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.dashboard.expression.v1.FieldIndexMap;
import stroom.dashboard.expression.v1.Val;
import stroom.query.api.v2.Param;
import stroom.query.api.v2.SearchRequest;
import stroom.query.common.v2.*;
import stroom.statistics.impl.sql.shared.StatisticStoreDoc;
import stroom.task.api.TaskContext;
import stroom.task.api.TaskContextFactory;
import stroom.util.logging.LambdaLogUtil;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;
import stroom.util.logging.LogUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class SqlStatisticsStore implements Store {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlStatisticsStore.class);
    private static final LambdaLogger LAMBDA_LOGGER = LambdaLoggerFactory.getLogger(SqlStatisticsStore.class);

    public static final String TASK_NAME = "Sql Statistic Search";

    private static final Duration RESULT_SEND_INTERVAL = Duration.ofSeconds(1);

    private final ResultHandler resultHandler;
    private final int resultHandlerBatchSize;
    private final Sizes defaultMaxResultsSizes;
    private final Sizes storeSize;
    private final CompletionState completionState;
    private final List<String> errors = Collections.synchronizedList(new ArrayList<>());
    private final String searchKey;
    private final TaskContextFactory taskContextFactory;
    private CompositeDisposable compositeDisposable;

    SqlStatisticsStore(final SearchRequest searchRequest,
                       final StatisticStoreDoc statisticStoreDoc,
                       final StatisticsSearchService statisticsSearchService,
                       final Sizes defaultMaxResultsSizes,
                       final Sizes storeSize,
                       final int resultHandlerBatchSize,
                       final Executor executor,
                       final TaskContextFactory taskContextFactory) {

        this.defaultMaxResultsSizes = defaultMaxResultsSizes;
        this.storeSize = storeSize;
        this.searchKey = searchRequest.getKey().toString();
        this.taskContextFactory = taskContextFactory;
        this.resultHandlerBatchSize = resultHandlerBatchSize;
        this.completionState = new CompletionState();

        final CoprocessorSettingsMap coprocessorSettingsMap = CoprocessorSettingsMap.create(searchRequest);
        Preconditions.checkNotNull(coprocessorSettingsMap);

        final FieldIndexMap fieldIndexMap = new FieldIndexMap(true);
        final Map<String, String> paramMap = getParamMap(searchRequest);

        final Map<CoprocessorSettingsMap.CoprocessorKey, Coprocessor> coprocessorMap = getCoprocessorMap(
                coprocessorSettingsMap, fieldIndexMap, paramMap);

        // convert the search into something stats understands
        final FindEventCriteria criteria = StatStoreCriteriaBuilder.buildCriteria(searchRequest, statisticStoreDoc);

        resultHandler = new SearchResultHandler(
                completionState, coprocessorSettingsMap, defaultMaxResultsSizes, storeSize);

        taskContextFactory.context(TASK_NAME, parentTaskContext -> {

            //get the flowable for the search results
            final Flowable<Val[]> searchResultsFlowable = statisticsSearchService.search(parentTaskContext,
                    statisticStoreDoc, criteria, fieldIndexMap);

            this.compositeDisposable = startAsyncSearch(parentTaskContext, searchResultsFlowable, coprocessorMap, executor);

            LOGGER.debug("Async search task started for key {}", searchKey);

        }).run();
    }


    @Override
    public void destroy() {
        LOGGER.debug("destroy called");
        //terminate the search
        // TODO this may need to change in 6.1
        compositeDisposable.clear();
    }

    @Override
    public boolean isComplete() {
        return completionState.isComplete();
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        completionState.awaitCompletion();
    }

    @Override
    public boolean awaitCompletion(final long timeout, final TimeUnit unit) throws InterruptedException {
        // Results are currently assembled synchronously in getMeta so the store is always complete.
        return completionState.awaitCompletion(timeout, unit);
    }

    @Override
    public Data getData(String componentId) {
        LOGGER.debug("getMeta called for componentId {}", componentId);

        return resultHandler.getResultStore(componentId);
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public List<String> getHighlights() {
        return null;
    }

    @Override
    public Sizes getDefaultMaxResultsSizes() {
        return defaultMaxResultsSizes;
    }

    @Override
    public Sizes getStoreSize() {
        return storeSize;
    }

    @Override
    public String toString() {
        return "SqlStatisticsStore{" +
                "defaultMaxResultsSizes=" + defaultMaxResultsSizes +
                ", storeSize=" + storeSize +
                ", completionState=" + completionState +
//                ", isTerminated=" + isTerminated +
                ", searchKey='" + searchKey + '\'' +
                '}';
    }

    private Map<String, String> getParamMap(final SearchRequest searchRequest) {
        final Map<String, String> paramMap;
        if (searchRequest.getQuery().getParams() != null) {
            paramMap = searchRequest.getQuery().getParams().stream()
                    .collect(Collectors.toMap(Param::getKey, Param::getValue));
        } else {
            paramMap = Collections.emptyMap();
        }
        return paramMap;
    }

    private CompositeDisposable startAsyncSearch(final TaskContext parentContext,
                                                 final Flowable<Val[]> searchResultsFlowable,
                                                 final Map<CoprocessorSettingsMap.CoprocessorKey, Coprocessor> coprocessorMap,
                                                 final Executor executor) {
        LOGGER.debug("Starting search with key {}", searchKey);
        parentContext.info(() -> "Sql Statistics search " + searchKey + " - running query");

        final LongAdder counter = new LongAdder();
        // subscribe to the flowable, mapping each resultSet to a String[]
        // Every n secs or m records process the results so far to send them to the result handler
        // If the task is canceled, the flowable produced by search() will stop emitting
        // Set up the results flowable, the search wont be executed until subscribe is called
        final Scheduler scheduler = Schedulers.from(executor);
        final AtomicLong nextProcessPayloadsTime = new AtomicLong(Instant.now().plus(RESULT_SEND_INTERVAL).toEpochMilli());
        final AtomicLong countSinceLastSend = new AtomicLong(0);
        final Instant queryStart = Instant.now();

        // TODO this may need to change in 6.1 due to differences in task termination
        // concatMapping a just() is a bit of a hack to ensure we have a single thread for task
        // monitoring and termination purposes.
        final CompositeDisposable compositeDisposable = new CompositeDisposable();

        final Disposable searchResultsDisposable = Flowable.just(0)
                .subscribeOn(scheduler)
                .concatMap(val -> searchResultsFlowable)
                .doOnSubscribe(subscription -> LOGGER.debug("doOnSubscribeCalled"))
                .subscribe(
                        data ->
                                taskContextFactory.context(parentContext, TASK_NAME, taskContext -> {
                                    counter.increment();
                                    countSinceLastSend.incrementAndGet();
                                    LAMBDA_LOGGER.trace(() -> String.format("data: [%s]", Arrays.toString(data)));

                                    // give the data array to each of our coprocessors
                                    coprocessorMap.values().forEach(coprocessor ->
                                            coprocessor.receive(data));
                                    // send what we have every 1s or when the batch reaches a set size
                                    long now = System.currentTimeMillis();
                                    if (now >= nextProcessPayloadsTime.get() ||
                                            countSinceLastSend.get() >= resultHandlerBatchSize) {

                                        LAMBDA_LOGGER.debug(LambdaLogUtil.message("{} vs {}, {} vs {}",
                                                now, nextProcessPayloadsTime,
                                                countSinceLastSend.get(), resultHandlerBatchSize));

                                        processPayloads(resultHandler, coprocessorMap);
                                        taskContext.info(() -> searchKey +
                                                " - running database query (" + counter.longValue() + " rows fetched)");
                                        nextProcessPayloadsTime.set(Instant.now().plus(RESULT_SEND_INTERVAL).toEpochMilli());
                                        countSinceLastSend.set(0);
                                    }

                                }).run(),

                        throwable -> {
                            LOGGER.error("Error in windowed flow: {}", throwable.getMessage(), throwable);
                            errors.add(throwable.getMessage());
                        },
                        () -> {
                            LAMBDA_LOGGER.debug(() ->
                                    String.format("onComplete of flowable called, counter: %s",
                                            counter.longValue()));
                            // completed our window so create and pass on a payload for the
                            // data we have gathered so far
                            processPayloads(resultHandler, coprocessorMap);
                            parentContext.info(() -> searchKey + " - complete");
                            completeSearch();

                            LAMBDA_LOGGER.debug(() ->
                                    LogUtil.message("Query finished in {}", Duration.between(queryStart, Instant.now())));
                        });

        LOGGER.debug("Out of flowable");

        compositeDisposable.add(searchResultsDisposable);

        return compositeDisposable;
    }

    private Map<CoprocessorSettingsMap.CoprocessorKey, Coprocessor> getCoprocessorMap(
            final CoprocessorSettingsMap coprocessorSettingsMap,
            final FieldIndexMap fieldIndexMap,
            final Map<String, String> paramMap) {

        return coprocessorSettingsMap.getMap()
                .entrySet()
                .stream()
                .map(entry -> Maps.immutableEntry(
                        entry.getKey(),
                        createCoprocessor(entry.getValue(), fieldIndexMap, paramMap)))
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void completeSearch() {
        LOGGER.debug("completeSearch called");
        completionState.complete();
    }

    /**
     * Synchronized to ensure multiple threads don't fight over the coprocessors which is unlikely to
     * happen anyway as it is mostly used in
     */
    private synchronized void processPayloads(final ResultHandler resultHandler,
                                              final Map<CoprocessorSettingsMap.CoprocessorKey, Coprocessor> coprocessorMap) {

        if (!Thread.currentThread().isInterrupted()) {
            LAMBDA_LOGGER.debug(() ->
                    LogUtil.message("processPayloads called for {} coprocessors", coprocessorMap.size()));

            //build a payload map from whatever the coprocessors have in them, if anything
            final Map<CoprocessorSettingsMap.CoprocessorKey, Payload> payloadMap = coprocessorMap.entrySet().stream()
                    .map(entry ->
                            Maps.immutableEntry(entry.getKey(), entry.getValue().createPayload()))
                    .filter(entry ->
                            entry.getValue() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // log the queue sizes in the payload map
            if (LOGGER.isDebugEnabled()) {
                final String contents = payloadMap.entrySet().stream()
                        .map(entry -> {
                            String key = entry.getKey() != null ? entry.getKey().toString() : "null";
                            String size;
                            // entry checked for null in stream above
                            if (entry.getValue() instanceof TablePayload) {
                                TablePayload tablePayload = (TablePayload) entry.getValue();
                                if (tablePayload.getQueue() != null) {
                                    size = Integer.toString(tablePayload.getQueue().size());
                                } else {
                                    size = "null";
                                }
                            } else {
                                size = "?";
                            }
                            return key + ": " + size;
                        })
                        .collect(Collectors.joining(", "));
                LOGGER.debug("payloadMap: [{}]", contents);
            }

            // give the processed results to the collector, it will handle nulls
            resultHandler.handle(payloadMap);
        } else {
            LOGGER.debug("Thread is interrupted, not processing payload");
        }
    }

    private static Coprocessor createCoprocessor(final CoprocessorSettings settings,
                                                 final FieldIndexMap fieldIndexMap,
                                                 final Map<String, String> paramMap) {
        if (settings instanceof TableCoprocessorSettings) {
            final TableCoprocessorSettings tableCoprocessorSettings = (TableCoprocessorSettings) settings;
            return new TableCoprocessor(tableCoprocessorSettings, fieldIndexMap, paramMap);
        }
        return null;
    }
}
