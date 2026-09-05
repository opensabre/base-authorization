package io.github.opensabre.authorization.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that deployments can align issued token claims with their externally configured issuer.
 */
class IssuerConfigurationTest {

    @Test
    void authIssuerEnvironmentVariableOverridesTheDefaultIssuer() throws Exception {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("testEnvironment", Map.of(
                "AUTH_ISSUER_URI", "http://opensabre:8000"
        )));
        String configuredIssuer = "${AUTH_ISSUER_URI:http://www.opensabre.cloud:8000}";
        String applicationYaml = new FileSystemResource("src/main/resources/application.yml")
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(applicationYaml.contains("issuer-uri: " + configuredIssuer));
        assertEquals("http://opensabre:8000", environment.resolvePlaceholders(configuredIssuer));
    }
}
