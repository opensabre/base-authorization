package io.github.opensabre.authorization.oauth2.login;

import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.governance.audit.entity.AuditInfo;
import io.github.opensabre.governance.audit.event.AuditEvent;
import io.github.opensabre.webmvc.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发布认证生命周期审计事件，避免登录流程记录口令或验证码等敏感请求内容。
 */
@Component
@RequiredArgsConstructor
public class AuthenticationAuditPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 记录一次成功登录。
     *
     * @param request 当前 HTTP 请求
     * @param username 已认证用户名
     */
    public void recordLoginSuccess(HttpServletRequest request, String username) {
        publish(request, username, OperationType.LOGIN, "用户登录", StringUtils.EMPTY);
    }

    /**
     * 记录一次失败登录，不包含账号密码等敏感参数。
     *
     * @param request 当前 HTTP 请求
     * @param username 尝试登录的用户名
     * @param error 失败原因代码
     */
    public void recordLoginFailure(HttpServletRequest request, String username, String error) {
        publish(request, username, OperationType.LOGIN, "用户登录失败", error);
    }

    /**
     * 记录一次用户登出。
     *
     * @param request 当前 HTTP 请求
     * @param username 当前用户名
     */
    public void recordLogout(HttpServletRequest request, String username) {
        publish(request, username, OperationType.LOGOUT, "用户登出", StringUtils.EMPTY);
    }

    private void publish(HttpServletRequest request, String username, OperationType operationType,
                         String description, String errorMessage) {
        AuditInfo auditInfo = AuditInfo.builder()
                .operationType(operationType)
                .operationTime(new Date())
                .operatorUsername(StringUtils.defaultIfBlank(username, "anonymous"))
                .description(description)
                .module("AUTH")
                .clientIp(HttpUtils.getClientIpAddress(request))
                .userAgent(HttpUtils.getUserAgent(request))
                .requestMethod(request.getMethod())
                .requestUrl(request.getRequestURI())
                .errorMessage(errorMessage)
                .executionTime(0L)
                .targetKey(StringUtils.defaultIfBlank(username, "anonymous"))
                .build();
        applicationEventPublisher.publishEvent(new AuditEvent(auditInfo));
    }
}
