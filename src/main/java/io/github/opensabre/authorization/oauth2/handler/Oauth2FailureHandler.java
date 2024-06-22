package io.github.opensabre.authorization.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opensabre.authorization.exception.AuthErrorType;
import io.github.opensabre.common.core.entity.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class Oauth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        log.warn("authentication error:", exception);
        String message;
        if (exception instanceof OAuth2AuthenticationException auth2AuthenticationException) {
            OAuth2Error error = auth2AuthenticationException.getError();
            message = "认证信息错误: " + error.getErrorCode() + ", " + error.getDescription();
        } else if (exception instanceof BadCredentialsException) {
            message = "账户或密码错误!";
        } else if (exception instanceof LockedException) {
            message = "账户被锁定，请联系管理员!";
        } else if (exception instanceof AccountExpiredException) {
            message = "账户过期，请联系管理员!";
        } else if (exception instanceof DisabledException) {
            message = "账户被禁用，请联系管理员!";
        } else {
            message = exception.getMessage();
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail(AuthErrorType.UNAUTHORIZED, message)));
        response.getWriter().flush();
    }
}