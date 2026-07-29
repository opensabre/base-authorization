package io.github.opensabre.authorization.task;

import io.github.opensabre.authorization.config.OAuth2AuthorizationCleanupProperties;
import io.github.opensabre.authorization.service.impl.OAuth2AuthorizationRecordService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class OAuth2AuthorizationCleanupTask {

    private final OAuth2AuthorizationRecordService service;
    private final OAuth2AuthorizationCleanupProperties properties;
    private final MeterRegistry meterRegistry;
    private final OAuth2AuthorizationCleanupAuditPublisher auditPublisher;
    private final AtomicBoolean running = new AtomicBoolean();

    public OAuth2AuthorizationCleanupTask(OAuth2AuthorizationRecordService service,
                                          OAuth2AuthorizationCleanupProperties properties,
                                          MeterRegistry meterRegistry,
                                          OAuth2AuthorizationCleanupAuditPublisher auditPublisher) {
        this.service = service;
        this.properties = properties;
        this.meterRegistry = meterRegistry;
        this.auditPublisher = auditPublisher;
    }

    @Scheduled(cron = "${opensabre.oauth2.authorization.cleanup.cron:0 0 * * * *}")
    public void cleanup() {
        if (!properties.isEnabled() || !running.compareAndSet(false, true)) {
            return;
        }
        Timer.Sample sample = Timer.start(meterRegistry);
        long startedAt = System.currentTimeMillis();
        int deleted = 0;
        try {
            deleted = service.cleanupExpiredBefore(Instant.now().minus(properties.getRetention()));
            Counter.builder("opensabre.oauth2.authorization.cleanup.deleted")
                    .register(meterRegistry).increment(deleted);
            Counter.builder("opensabre.oauth2.authorization.cleanup.runs")
                    .tag("result", "success").register(meterRegistry).increment();
            log.info("OAuth2 authorization cleanup completed, deleted={}", deleted);
            auditPublisher.publish(true, deleted, System.currentTimeMillis() - startedAt, null);
        } catch (RuntimeException exception) {
            Counter.builder("opensabre.oauth2.authorization.cleanup.runs")
                    .tag("result", "failure").register(meterRegistry).increment();
            log.error("OAuth2 authorization cleanup failed; completed batches remain deleted and the next run will resume",
                    exception);
            auditPublisher.publish(false, deleted, System.currentTimeMillis() - startedAt,
                    exception.getClass().getSimpleName());
        } finally {
            sample.stop(Timer.builder("opensabre.oauth2.authorization.cleanup.duration")
                    .register(meterRegistry));
            running.set(false);
        }
    }
}
