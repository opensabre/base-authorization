package io.github.opensabre.authorization.oauth2.handler;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

import static org.assertj.core.api.Assertions.assertThat;

class Oauth2FailureHandlerTest {

    @Test
    void shouldPreserveStandardOauthErrorForTokenClients() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2Error error = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_GRANT,
                "Refresh token is inactive",
                null);

        new Oauth2FailureHandler().onAuthenticationFailure(
                new MockHttpServletRequest(),
                response,
                new OAuth2AuthenticationException(error));

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains("\"error\":\"invalid_grant\"");
        assertThat(response.getContentAsString()).contains("\"error_description\":\"Refresh token is inactive\"");
        assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
        assertThat(response.getHeader("Pragma")).isEqualTo("no-cache");
    }
}
