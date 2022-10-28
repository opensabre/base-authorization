package com.springboot.auth.authorization.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SmsCodeProviderFallback implements SmsCodeProvider {

    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public String getSmsCode(String mobile, String businessType) {
        // 该类为mock，目前暂时没有sms的服务，团队为123456
        log.warn("getUserByUniqueId downgrade:mobile={},businessType={}", mobile, businessType);
        return passwordEncoder.encode("123456");
    }
}
