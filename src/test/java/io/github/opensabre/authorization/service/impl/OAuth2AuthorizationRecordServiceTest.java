package io.github.opensabre.authorization.service.impl;

import io.github.opensabre.authorization.dao.OAuth2AuthorizationRecordRepository;
import io.github.opensabre.authorization.config.OAuth2AuthorizationCleanupProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

class OAuth2AuthorizationRecordServiceTest {

    private final OAuth2AuthorizationRecordRepository repository =
            mock(OAuth2AuthorizationRecordRepository.class);
    private final OAuth2AuthorizationService authorizationService =
            mock(OAuth2AuthorizationService.class);
    private final OAuth2AuthorizationRecordService service =
            new OAuth2AuthorizationRecordService(repository, authorizationService, cleanupProperties());

    @Test
    void shouldRemoveExistingAuthorizationWhenRevoked() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorizationService.findById("authorization-id")).thenReturn(authorization);

        assertThat(service.revoke("authorization-id")).isTrue();
        verify(authorizationService).remove(authorization);
    }

    @Test
    void shouldReturnFalseWhenAuthorizationDoesNotExist() {
        when(authorizationService.findById("missing")).thenReturn(null);

        assertThat(service.revoke("missing")).isFalse();
    }

    @Test
    void shouldDeleteExpiredAuthorizationsUsingCurrentCutoff() {
        when(repository.deleteExpiredBatch(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(500)))
                .thenReturn(3);

        assertThat(service.cleanupExpired()).isEqualTo(3);
        verify(repository).deleteExpiredBatch(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(500));
    }

    @Test
    void shouldStopAtConfiguredBatchLimit() {
        OAuth2AuthorizationCleanupProperties properties = new OAuth2AuthorizationCleanupProperties();
        properties.setBatchSize(2);
        properties.setMaxBatchesPerRun(3);
        OAuth2AuthorizationRecordService boundedService =
                new OAuth2AuthorizationRecordService(repository, authorizationService, properties);
        when(repository.deleteExpiredBatch(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(2)))
                .thenReturn(2);

        assertThat(boundedService.cleanupExpired()).isEqualTo(6);
        verify(repository, times(3))
                .deleteExpiredBatch(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(2));
    }

    private static OAuth2AuthorizationCleanupProperties cleanupProperties() {
        return new OAuth2AuthorizationCleanupProperties();
    }
}
