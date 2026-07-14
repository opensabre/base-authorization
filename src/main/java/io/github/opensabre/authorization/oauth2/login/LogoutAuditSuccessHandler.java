package io.github.opensabre.authorization.oauth2.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 在 Spring Security 清除认证信息后记录用户登出事件。
 */
@Component
@RequiredArgsConstructor
public class LogoutAuditSuccessHandler implements LogoutSuccessHandler {

    private final AuthenticationAuditPublisher authenticationAuditPublisher;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        authenticationAuditPublisher.recordLogout(request, authentication == null ? null : authentication.getName());
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
