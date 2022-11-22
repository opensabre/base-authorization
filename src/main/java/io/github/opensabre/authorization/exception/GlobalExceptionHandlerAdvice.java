package io.github.opensabre.authorization.exception;

import io.github.opensabre.common.core.entity.vo.Result;
import io.github.opensabre.common.web.exception.DefaultGlobalExceptionHandlerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice extends DefaultGlobalExceptionHandlerAdvice {

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class})
    public Result internalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        log.error("load user by username exception:{}", ex.getMessage());
        return Result.fail(AuthErrorType.UNAUTHORIZED_CLIENT);
    }
}