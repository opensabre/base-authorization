package io.github.opensabre.authorization.service.impl;

import io.github.opensabre.authorization.dao.OAuth2AuthorizationRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuth2AuthorizationRecordServiceTest {

    private final OAuth2AuthorizationRecordRepository repository =
            mock(OAuth2AuthorizationRecordRepository.class);
    private final OAuth2AuthorizationService authorizationService =
            mock(OAuth2AuthorizationService.class);
    private final OAuth2AuthorizationRecordService service =
            new OAuth2AuthorizationRecordService(repository, authorizationService);

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
        when(repository.deleteExpired(org.mockito.ArgumentMatchers.any())).thenReturn(3);

        assertThat(service.cleanupExpired()).isEqualTo(3);
        verify(repository).deleteExpired(org.mockito.ArgumentMatchers.any());
    }
}
