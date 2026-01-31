package io.github.opensabre.authorization.provider;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;

/**
 * 实现短信验证码的服务
 */
@FeignClient(name = "base-sysadmin", fallback = OrganizationProviderFallback.class)
public interface SmsCodeProvider {

    /**
     * @param phoneNo 手机号
     * @return String 验证码
     */
    @GetMapping(value = "/captcha/send/sms")
    String getSmsCode(@RequestPart("phoneNo") String phoneNo, @RequestPart("scenario") String scenario);
}
