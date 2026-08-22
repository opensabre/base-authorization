package io.github.opensabre.authorization.provider;

import io.github.opensabre.authorization.exception.AuthErrorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CaptchaProviderFallbackTest {

    @Test
    void shouldReturnFailureForImageCaptchaWhenDependencyIsUnavailable() {
        var result = new CaptchaProviderFallback(new IllegalStateException("downstream unavailable"))
                .sendImageCaptcha("LOGIN_IMAGE", "session-1");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCode()).isEqualTo(AuthErrorType.CAPTCHA_SERVICE_UNAVAILABLE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    void shouldReturnFailureForVerificationWhenDependencyIsUnavailable() {
        var result = new CaptchaProviderFallback(new IllegalStateException("downstream unavailable"))
                .verifyCaptcha("LOGIN_IMAGE", "captcha-1", "1234");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCode()).isEqualTo(AuthErrorType.CAPTCHA_SERVICE_UNAVAILABLE.getCode());
        assertThat(result.getData()).isNull();
    }
}
