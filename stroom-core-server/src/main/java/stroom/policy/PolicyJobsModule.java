package stroom.policy;

import stroom.task.api.job.ScheduledJobsModule;
import stroom.task.api.job.TaskConsumer;

import javax.inject.Inject;

import static stroom.task.api.job.Schedule.ScheduleType.CRON;

public class PolicyJobsModule extends ScheduledJobsModule {
    @Override
    protected void configure() {
        super.configure();
        bindJob()
                .name("Data Retention")
                .description("Delete data that exceeds the retention period specified by data retention policy")
                .schedule(CRON, "0 0 *")
                .to(DataRetention.class);
    }

    private static class DataRetention extends TaskConsumer {
        @Inject
        DataRetention(final DataRetentionExecutor dataRetentionExecutor) {
            super(task -> dataRetentionExecutor.exec());
        }
    }
}
