package io.github.opensabre.authorization.task;

import io.github.opensabre.eda.api.EdaEvent;
import io.github.opensabre.eda.api.EdaEventPublisher;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.github.opensabre.governance.audit.entity.AuditInfo;
import io.github.opensabre.governance.audit.event.DefaultAuditEventHandler;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class OAuth2AuthorizationCleanupAuditPublisher {

    private final EdaEventPublisher eventPublisher;

    public void publish(boolean success, int deleted, long executionTime, String errorMessage) {
        AuditInfo auditInfo = AuditInfo.builder()
                .operationType(OperationType.DELETE)
                .operationTime(new Date())
                .operatorUsername("system")
                .description("定时清理OAuth2授权记录")
                .module("OAUTH2_AUTHORIZATION")
                .requestMethod("SCHEDULED")
                .requestUrl("oauth2-authorization-cleanup")
                .errorMessage(StringUtils.defaultString(errorMessage))
                .executionTime(executionTime)
                .targetKey("deleted=" + deleted + ",success=" + success)
                .build();
        eventPublisher.publishLocal(
                EdaEvent.of(DefaultAuditEventHandler.EVENT_TYPE, "authorization", auditInfo));
    }
}
