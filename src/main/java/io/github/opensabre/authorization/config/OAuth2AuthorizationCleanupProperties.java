package io.github.opensabre.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "opensabre.oauth2.authorization.cleanup")
public class OAuth2AuthorizationCleanupProperties {

    private boolean enabled;
    private Duration retention = Duration.ofDays(7);
    private int batchSize = 500;
    private int maxBatchesPerRun = 20;
}
