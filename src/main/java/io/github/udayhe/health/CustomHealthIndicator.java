package io.github.udayhe.health;

import io.micronaut.context.annotation.Requires;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import static io.micronaut.core.async.publisher.Publishers.just;
import static java.lang.System.currentTimeMillis;
import static java.time.Instant.ofEpochMilli;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * @author udayhegde
 */
@Singleton
@Requires(beans = HealthEndpoint.class)
public class CustomHealthIndicator implements HealthIndicator {

    private Long serviceStartTime = 0L;


    @PostConstruct
    public void init() {
        serviceStartTime = currentTimeMillis();
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return just(getHealthResult());
    }

    private HealthResult getHealthResult() {
        return HealthResult.builder("health", HealthStatus.UP).
                details(ofEntries(
                        entry("since", ofEpochMilli(serviceStartTime).toString()),
                        entry("now", ofEpochMilli(currentTimeMillis()).toString())
                )).build();
    }

}
