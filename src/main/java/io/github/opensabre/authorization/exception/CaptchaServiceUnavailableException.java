package io.github.opensabre.authorization.exception;

import io.github.opensabre.common.core.exception.BaseException;

/**
 * Indicates that the captcha dependency cannot serve the request.
 */
public class CaptchaServiceUnavailableException extends BaseException {

    public CaptchaServiceUnavailableException() {
        super(AuthErrorType.CAPTCHA_SERVICE_UNAVAILABLE);
    }
}
