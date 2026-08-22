package io.github.opensabre.authorization.rest;

import io.github.opensabre.authorization.entity.CaptchaVo;
import io.github.opensabre.authorization.exception.CaptchaServiceUnavailableException;
import io.github.opensabre.authorization.oauth2.login.LoginSecurityProperties;
import io.github.opensabre.authorization.provider.CaptchaProvider;
import io.github.opensabre.common.core.entity.vo.Result;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginCaptchaControllerTest {

    @Test
    void shouldReturnCaptchaDataForSuccessfulProviderResponse() {
        CaptchaProvider provider = mock(CaptchaProvider.class);
        LoginSecurityProperties properties = new LoginSecurityProperties();
        HttpSession session = mock(HttpSession.class);
        CaptchaVo captcha = new CaptchaVo();
        when(session.getId()).thenReturn("session-1");
        when(provider.sendImageCaptcha("LOGIN_IMAGE", "session-1")).thenReturn(Result.success(captcha));

        CaptchaVo result = new LoginCaptchaController(provider, properties).imageCaptcha(session);

        assertThat(result).isSameAs(captcha);
    }

    @Test
    void shouldRejectNullOrFailedProviderResponse() {
        CaptchaProvider provider = mock(CaptchaProvider.class);
        LoginSecurityProperties properties = new LoginSecurityProperties();
        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn("session-1");
        when(provider.sendImageCaptcha("LOGIN_IMAGE", "session-1"))
                .thenReturn(Result.fail(io.github.opensabre.authorization.exception.AuthErrorType.CAPTCHA_SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> new LoginCaptchaController(provider, properties).imageCaptcha(session))
                .isInstanceOf(CaptchaServiceUnavailableException.class);
    }
}
