package io.github.opensabre.authorization.oauth2.device;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests device client authentication request conversion.
 */
class DeviceClientAuthenticationConverterTest {

    private static final String DEVICE_AUTHORIZATION_ENDPOINT = "/oauth2/device_authorization";

    private final DeviceClientAuthenticationConverter converter =
            new DeviceClientAuthenticationConverter(DEVICE_AUTHORIZATION_ENDPOINT);

    @Test
    void shouldConvertRequestWithSingleClientId() {
        MockHttpServletRequest request = deviceAuthorizationRequest();
        request.addParameter(OAuth2ParameterNames.CLIENT_ID, "device-client");

        assertThat(converter.convert(request))
                .isInstanceOf(DeviceClientAuthenticationToken.class)
                .extracting("principal")
                .isEqualTo("device-client");
    }

    @Test
    void shouldRejectRequestWithDuplicateClientId() {
        MockHttpServletRequest request = deviceAuthorizationRequest();
        request.addParameter(OAuth2ParameterNames.CLIENT_ID, "device-client", "other-client");

        assertThatThrownBy(() -> converter.convert(request))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    private MockHttpServletRequest deviceAuthorizationRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", DEVICE_AUTHORIZATION_ENDPOINT);
        request.setServletPath(DEVICE_AUTHORIZATION_ENDPOINT);
        request.addParameter(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.DEVICE_CODE.getValue());
        request.addParameter(OAuth2ParameterNames.DEVICE_CODE, "device-code");
        return request;
    }
}
