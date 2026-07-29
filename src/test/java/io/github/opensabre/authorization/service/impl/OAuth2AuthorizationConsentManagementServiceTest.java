package io.github.opensabre.authorization.service.impl;

import io.github.opensabre.authorization.dao.OAuth2AuthorizationConsentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuth2AuthorizationConsentManagementServiceTest {

    private final OAuth2AuthorizationConsentRepository repository =
            mock(OAuth2AuthorizationConsentRepository.class);
    private final OAuth2AuthorizationConsentService consentService =
            mock(OAuth2AuthorizationConsentService.class);
    private final OAuth2AuthorizationConsentManagementService service =
            new OAuth2AuthorizationConsentManagementService(repository, consentService);

    @Test
    void shouldRemoveExistingConsent() {
        OAuth2AuthorizationConsent consent = mock(OAuth2AuthorizationConsent.class);
        when(consentService.findById("registered-client-id", "alice")).thenReturn(consent);

        assertThat(service.remove("registered-client-id", "alice")).isTrue();
        verify(consentService).remove(consent);
    }

    @Test
    void shouldReturnFalseWhenConsentDoesNotExist() {
        when(consentService.findById("registered-client-id", "missing")).thenReturn(null);

        assertThat(service.remove("registered-client-id", "missing")).isFalse();
    }
}
