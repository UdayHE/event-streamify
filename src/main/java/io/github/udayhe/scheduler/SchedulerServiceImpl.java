package io.github.udayhe.scheduler;

import io.github.udayhe.service.EventStreamService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.github.udayhe.util.Constants.SCHEDULER_DELAY;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.ofEpochMilli;

/**
 * @author udayhegde
 */
@Slf4j
@Singleton
@RequiredArgsConstructor
@Requires(property = "redis.enable", value = StringUtils.TRUE)
public class SchedulerServiceImpl implements SchedulerService {


    private final EventStreamService eventStreamService;
    private Long schedulerStartTime = 0L;
    private Long schedulerEndTime = 0L;
    private String threadName;

    @Scheduled(fixedDelay = SCHEDULER_DELAY)
    void executeInFixedDelay() {
        schedulerStartTime = currentTimeMillis();
        threadName = Thread.currentThread().getName();
        log.info("executeEvery:{} Seconds..", SCHEDULER_DELAY);
        eventStreamService.updateCacheFromChannelProcessorMap();
        schedulerEndTime = currentTimeMillis();
    }

    public String getSchedulerExecutionTime() {
        return "ThreadName:" + threadName + "\nschedulerStartTime:" + ofEpochMilli(schedulerStartTime)
                + "\nschedulerEndTime:" + ofEpochMilli(schedulerEndTime)
                + "\nTotalTimeTaken:" + (schedulerEndTime - schedulerStartTime);
    }
}
