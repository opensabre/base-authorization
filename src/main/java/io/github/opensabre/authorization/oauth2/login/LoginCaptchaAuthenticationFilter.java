package io.github.opensabre.authorization.oauth2.login;

import io.github.opensabre.authorization.provider.CaptchaProvider;
import io.github.opensabre.common.core.entity.vo.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCaptchaAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher loginRequestMatcher = new AntPathRequestMatcher("/login", HttpMethod.POST.name());
    private final LoginSecurityService loginSecurityService;
    private final LoginSecurityProperties loginSecurityProperties;
    private final CaptchaProvider captchaProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!loginRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getParameter("username");
        if (!loginSecurityService.isCaptchaRequired(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        String captchaId = request.getParameter("captchaId");
        String captchaCode = request.getParameter("captchaCode");
        if (StringUtils.isAnyBlank(captchaId, captchaCode) || !verifyCaptcha(captchaId, captchaCode)) {
            log.warn("Login captcha verification failed: username={}", username);
            response.sendRedirect(LoginRedirects.failureUrl(request, username, "captcha"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean verifyCaptcha(String captchaId, String captchaCode) {
        Result<Boolean> result = captchaProvider.verifyCaptcha(loginSecurityProperties.getCaptchaScenario(), captchaId, captchaCode);
        return result != null && Boolean.TRUE.equals(result.getData());
    }
}
