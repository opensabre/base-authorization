package io.github.opensabre.authorization.rest;

import io.github.opensabre.authorization.entity.CaptchaVo;
import io.github.opensabre.authorization.exception.CaptchaServiceUnavailableException;
import io.github.opensabre.authorization.oauth2.login.LoginSecurityProperties;
import io.github.opensabre.authorization.provider.CaptchaProvider;
import io.github.opensabre.common.core.entity.vo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginCaptchaController {

    private final CaptchaProvider captchaProvider;
    private final LoginSecurityProperties loginSecurityProperties;

    public LoginCaptchaController(CaptchaProvider captchaProvider, LoginSecurityProperties loginSecurityProperties) {
        this.captchaProvider = captchaProvider;
        this.loginSecurityProperties = loginSecurityProperties;
    }

    @PostMapping("/login/captcha/image")
    public CaptchaVo imageCaptcha(HttpSession session) {
        Result<CaptchaVo> result = captchaProvider.sendImageCaptcha(loginSecurityProperties.getCaptchaScenario(), session.getId());
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new CaptchaServiceUnavailableException();
        }
        return result.getData();
    }
}
