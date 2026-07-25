package io.github.opensabre.authorization.config;

import io.github.opensabre.authorization.exception.AuthErrorType;
import io.github.opensabre.governance.errorcatalog.ErrorCatalogProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declares authorization business errors for the centralized error-code directory. */
@Configuration
public class ErrorCatalogConfiguration {
    @Bean
    public ErrorCatalogProvider authorizationErrorCatalogProvider() {
        return ErrorCatalogProvider.of("authorization", AuthErrorType.values());
    }
}
