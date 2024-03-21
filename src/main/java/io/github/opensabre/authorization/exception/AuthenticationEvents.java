package io.github.opensabre.authorization.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationEvents {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.info("{} 认证成功", successEvent.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureDisabledEvent) {
        log.error("{} 认证失败，失败原因：{}",
                failureDisabledEvent.getAuthentication().getName(),
                failureDisabledEvent.getException().getMessage());
    }
}

