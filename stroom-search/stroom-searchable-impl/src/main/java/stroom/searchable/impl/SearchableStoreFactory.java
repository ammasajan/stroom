package stroom.searchable.impl;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.docref.DocRef;
import stroom.query.api.v2.SearchRequest;
import stroom.query.common.v2.Sizes;
import stroom.query.common.v2.Store;
import stroom.query.common.v2.StoreFactory;
import stroom.searchable.api.Searchable;
import stroom.searchable.api.SearchableProvider;
import stroom.task.api.TaskContext;
import stroom.task.api.TaskContextFactory;
import stroom.ui.config.shared.UiConfig;
import stroom.util.logging.LambdaLogger;
import stroom.util.logging.LambdaLoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
class SearchableStoreFactory implements StoreFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchableStoreFactory.class);
    private static final LambdaLogger LAMBDA_LOGGER = LambdaLoggerFactory.getLogger(SearchableStoreFactory.class);
    private static final String TASK_NAME = "DB Search";

    private final Executor executor;
    private final TaskContextFactory taskContextFactory;
    private final SearchableConfig config;
    private final UiConfig clientConfig;
    private final SearchableProvider searchableProvider;

    @Inject
    SearchableStoreFactory(final Executor executor,
                           final TaskContextFactory taskContextFactory,
                           final SearchableConfig config,
                           final UiConfig clientConfig,
                           final SearchableProvider searchableProvider) {
        this.executor = executor;
        this.taskContextFactory = taskContextFactory;
        this.config = config;
        this.clientConfig = clientConfig;
        this.searchableProvider = searchableProvider;
    }

    @Override
    public Store create(final SearchRequest searchRequest) {
        return taskContextFactory.contextResult(TASK_NAME, taskContext -> {
            LOGGER.debug("create called for searchRequest {} ", searchRequest);

            final DocRef docRef = Preconditions.checkNotNull(
                    Preconditions.checkNotNull(
                            Preconditions.checkNotNull(searchRequest)
                                    .getQuery())
                            .getDataSource());
            Preconditions.checkNotNull(searchRequest.getResultRequests(), "searchRequest must have at least one resultRequest");
            Preconditions.checkArgument(!searchRequest.getResultRequests().isEmpty(), "searchRequest must have at least one resultRequest");

            final Searchable searchable = searchableProvider.get(docRef);

            Preconditions.checkNotNull(searchable, "Searchable could not be found for " + docRef);

            return buildStore(taskContext, searchRequest, searchable);
        }).get();
    }

    private Store buildStore(final TaskContext taskContext,
                             final SearchRequest searchRequest,
                             final Searchable searchable) {
        Preconditions.checkNotNull(searchRequest);
        Preconditions.checkNotNull(searchable);

        final Sizes storeSize = getStoreSizes();
        final Sizes defaultMaxResultsSizes = getDefaultMaxResultsSizes();
        final int resultHandlerBatchSize = getResultHandlerBatchSize();

        //wrap the resultHandler in a new store, initiating the search in the process
        return new SearchableStore(
                defaultMaxResultsSizes,
                storeSize,
                resultHandlerBatchSize,
                searchable,
                taskContextFactory,
                taskContext,
                searchRequest,
                executor);
    }

    private Sizes getDefaultMaxResultsSizes() {
        final String value = clientConfig.getDefaultMaxResults();
        return extractValues(value);
    }

    private Sizes getStoreSizes() {
        final String value = config.getStoreSize();
        return extractValues(value);
    }

    private int getResultHandlerBatchSize() {
        return 5000;
    }

    private Sizes extractValues(String value) {
        if (value != null) {
            try {
                return Sizes.create(Arrays.stream(value.split(","))
                        .map(String::trim)
                        .map(Integer::valueOf)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        }
        return Sizes.create(Integer.MAX_VALUE);
    }
}
