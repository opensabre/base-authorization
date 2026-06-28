package io.github.opensabre.authorization.provider;

import io.github.opensabre.authorization.entity.CaptchaVo;
import io.github.opensabre.common.core.entity.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "base-sysadmin", contextId = "captchaProvider", fallback = CaptchaProviderFallback.class)
public interface CaptchaProvider {

    @PostMapping(value = "/captcha/send/image")
    Result<CaptchaVo> sendImageCaptcha(@RequestParam("scenario") String scenario,
                                       @RequestParam("requestKey") String requestKey);

    @PostMapping(value = "/captcha/verify")
    Result<Boolean> verifyCaptcha(@RequestParam("scenario") String scenario,
                                  @RequestParam("captchaId") String captchaId,
                                  @RequestParam("inputCode") String inputCode);
}
