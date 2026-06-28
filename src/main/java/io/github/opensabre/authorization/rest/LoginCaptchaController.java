package io.github.opensabre.authorization.rest;

import io.github.opensabre.authorization.entity.CaptchaVo;
import io.github.opensabre.authorization.oauth2.login.LoginSecurityProperties;
import io.github.opensabre.authorization.provider.CaptchaProvider;
import io.github.opensabre.common.core.entity.vo.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginCaptchaController {

    @Resource
    private CaptchaProvider captchaProvider;

    @Resource
    private LoginSecurityProperties loginSecurityProperties;

    @PostMapping("/login/captcha/image")
    public CaptchaVo imageCaptcha(HttpSession session) {
        Result<CaptchaVo> result = captchaProvider.sendImageCaptcha(loginSecurityProperties.getCaptchaScenario(), session.getId());
        return result == null ? new CaptchaVo() : result.getData();
    }
}
