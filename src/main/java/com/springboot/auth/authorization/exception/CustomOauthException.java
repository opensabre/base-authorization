package com.springboot.auth.authorization.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.opensabre.common.core.entity.vo.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
class CustomOauthException extends OAuth2AuthenticationException {

    private final Result result;

    CustomOauthException(OAuth2AuthenticationException oAuth2Exception) {
        super(oAuth2Exception.getError(), oAuth2Exception);
        this.result = Result.fail(AuthErrorType.valueOf(oAuth2Exception.getError().getErrorCode().toUpperCase()), oAuth2Exception);
    }
}