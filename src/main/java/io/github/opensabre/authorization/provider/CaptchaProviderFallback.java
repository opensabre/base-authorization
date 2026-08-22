package io.github.opensabre.authorization.provider;

import io.github.opensabre.authorization.entity.CaptchaVo;
import io.github.opensabre.authorization.exception.AuthErrorType;
import io.github.opensabre.common.core.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaptchaProviderFallback implements CaptchaProvider {

    private final Throwable cause;

    public CaptchaProviderFallback() {
        this(null);
    }

    public CaptchaProviderFallback(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public Result<CaptchaVo> sendImageCaptcha(String scenario, String requestKey) {
        log.warn("captcha image service unavailable: scenario={}, requestKey={}", scenario, requestKey, cause);
        return Result.fail(AuthErrorType.CAPTCHA_SERVICE_UNAVAILABLE);
    }

    @Override
    public Result<Boolean> verifyCaptcha(String scenario, String captchaId, String inputCode) {
        log.warn("captcha verify service unavailable: scenario={}, captchaId={}", scenario, captchaId, cause);
        return Result.fail(AuthErrorType.CAPTCHA_SERVICE_UNAVAILABLE);
    }
}
