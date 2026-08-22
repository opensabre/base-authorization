package io.github.opensabre.authorization.provider;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Creates captcha fallbacks while preserving the downstream failure for logging.
 */
@Component
public class CaptchaProviderFallbackFactory implements FallbackFactory<CaptchaProvider> {

    @Override
    public CaptchaProvider create(Throwable cause) {
        return new CaptchaProviderFallback(cause);
    }
}
