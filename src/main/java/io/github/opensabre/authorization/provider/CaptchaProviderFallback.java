package io.github.opensabre.authorization.provider;

import io.github.opensabre.authorization.entity.CaptchaVo;
import io.github.opensabre.common.core.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CaptchaProviderFallback implements CaptchaProvider {

    @Override
    public Result<CaptchaVo> sendImageCaptcha(String scenario, String requestKey) {
        log.warn("sendImageCaptcha downgrade");
        return Result.success(new CaptchaVo());
    }

    @Override
    public Result<Boolean> verifyCaptcha(String scenario, String captchaId, String inputCode) {
        log.warn("verifyCaptcha downgrade");
        return Result.success(false);
    }
}
