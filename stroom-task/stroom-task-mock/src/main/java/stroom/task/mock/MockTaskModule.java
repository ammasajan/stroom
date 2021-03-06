package stroom.task.mock;

import stroom.task.api.TaskContext;
import stroom.task.api.TaskContextFactory;
import stroom.task.api.TaskManager;
import stroom.task.shared.TaskId;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MockTaskModule extends AbstractModule {

    @Provides
    TaskManager getTaskManager() {
        return new TaskManager() {
            @Override
            public void startup() {

            }

            @Override
            public void shutdown() {

            }

            @Override
            public boolean isTerminated(final TaskId taskId) {
                return false;
            }

            @Override
            public void terminate(final TaskId taskId) {

            }
        };
    }

    @Provides
    TaskContextFactory getTaskContextFactory() {
        return new TaskContextFactory() {
            @Override
            public Runnable context(final String taskName, final Consumer<TaskContext> consumer) {
                return () -> consumer.accept(createTaskContext());
            }

            @Override
            public Runnable context(final TaskContext parentContext, final String taskName, final Consumer<TaskContext> consumer) {
                return () -> consumer.accept(createTaskContext());
            }

            @Override
            public <R> Supplier<R> contextResult(final String taskName, final Function<TaskContext, R> function) {
                return () -> function.apply(createTaskContext());
            }

            @Override
            public <R> Supplier<R> contextResult(final TaskContext parentContext, final String taskName, final Function<TaskContext, R> function) {
                return () -> function.apply(createTaskContext());
            }

            @Override
            public TaskContext currentContext() {
                return createTaskContext();
            }
        };
    }

    private TaskContext createTaskContext() {
        return new TaskContext() {
            @Override
            public void info(final Supplier<String> messageSupplier) {
            }

            @Override
            public TaskId getTaskId() {
                return new TaskId() {
                    @Override
                    public String getId() {
                        return UUID.randomUUID().toString();
                    }
                };
            }
        };
    }
}
