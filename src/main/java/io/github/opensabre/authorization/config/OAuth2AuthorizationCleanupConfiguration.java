package io.github.opensabre.authorization.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OAuth2AuthorizationCleanupProperties.class)
public class OAuth2AuthorizationCleanupConfiguration {
}
