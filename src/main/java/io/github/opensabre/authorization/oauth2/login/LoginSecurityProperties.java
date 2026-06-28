package io.github.opensabre.authorization.oauth2.login;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "opensabre.login-security")
public class LoginSecurityProperties {

    /**
     * 连续密码错误达到该次数后，下次登录要求图形验证码。
     */
    private int captchaThreshold = 2;

    /**
     * 连续密码错误达到该次数后，锁定用户。
     */
    private int lockThreshold = 5;

    /**
     * 失败次数缓存有效期。不是账户锁定时长，只用于清理临时计数。
     */
    private Duration failureExpire = Duration.ofDays(1);

    private String captchaScenario = "LOGIN_IMAGE";
}
