package io.github.opensabre.authorization.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opensabre.authorization.exception.AuthErrorType;
import io.github.opensabre.common.core.entity.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class Oauth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        log.error("authentication error:", exception);
        String message;
        if (exception instanceof OAuth2AuthenticationException auth2AuthenticationException) {
            OAuth2Error error = auth2AuthenticationException.getError();
            message = "认证信息错误: " + error.getErrorCode() + ", " + error.getDescription();
        } else {
            message = exception.getMessage();
        }

        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(Result.fail(AuthErrorType.UNAUTHORIZED, message)));
        response.getWriter().flush();
    }
}