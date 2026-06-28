package io.github.opensabre.authorization.oauth2.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final LoginSecurityService loginSecurityService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        String error = "bad_credentials";

        if (exception instanceof BadCredentialsException) {
            int failureCount = loginSecurityService.recordPasswordFailure(username);
            log.warn("Login password failure: username={}, failureCount={}", username, failureCount);
        } else {
            error = exception.getClass().getSimpleName();
            log.warn("Login authentication failure: username={}, error={}", username, error, exception);
        }

        response.sendRedirect(LoginRedirects.failureUrl(request, username, error));
    }
}
