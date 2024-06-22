package io.github.opensabre.authorization.exception;

import io.github.opensabre.common.core.entity.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(100)
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> internalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        log.error("Authentication Exception:{}", ex.getMessage());
        return Result.fail(AuthErrorType.UNAUTHORIZED_CLIENT);
    }

    @ExceptionHandler(value = {OAuth2AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> internalAuthenticationServiceException(OAuth2AuthenticationException ex) {
        log.error("On Authentication Failure :{}", ex.getMessage());
        return Result.fail(AuthErrorType.INVALID_CLIENT);
    }
}