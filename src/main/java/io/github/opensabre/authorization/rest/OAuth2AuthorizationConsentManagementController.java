package io.github.opensabre.authorization.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.authorization.entity.form.OAuth2AuthorizationConsentQueryForm;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationConsentQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationConsentVo;
import io.github.opensabre.authorization.service.IOAuth2AuthorizationConsentManagementService;
import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端授权同意记录管理接口。
 */
@RestController
@RequestMapping("/authorization-consents")
@Tag(name = "客户端授权记录")
public class OAuth2AuthorizationConsentManagementController {

    private final IOAuth2AuthorizationConsentManagementService consentManagementService;

    public OAuth2AuthorizationConsentManagementController(
            IOAuth2AuthorizationConsentManagementService consentManagementService) {
        this.consentManagementService = consentManagementService;
    }

    @Operation(summary = "分页查询客户端授权记录")
    @PostMapping("/conditions")
    public IPage<OAuth2AuthorizationConsentVo> search(
            @Valid @RequestBody OAuth2AuthorizationConsentQueryForm form) {
        return consentManagementService.query(
                form.getPage(), form.toParam(OAuth2AuthorizationConsentQueryParam.class));
    }

    @Operation(summary = "查看客户端授权记录")
    @GetMapping
    public OAuth2AuthorizationConsentVo get(
            @Parameter(description = "注册客户端内部ID", required = true)
            @RequestParam String registeredClientId,
            @Parameter(description = "授权用户或主体", required = true)
            @RequestParam String principalName) {
        return consentManagementService.get(registeredClientId, principalName);
    }

    @Operation(
            summary = "删除客户端授权记录",
            description = "删除后用户再次发起授权时需要重新同意；已签发Token请在Token签发管理中终止")
    @DeleteMapping
    @Audit(
            operationType = OperationType.DELETE,
            description = "删除客户端授权记录",
            module = "OAUTH2_AUTHORIZATION_CONSENT",
            response = true,
            key = "#registeredClientId + ':' + #principalName")
    public Boolean remove(
            @Parameter(description = "注册客户端内部ID", required = true)
            @RequestParam String registeredClientId,
            @Parameter(description = "授权用户或主体", required = true)
            @RequestParam String principalName) {
        return consentManagementService.remove(registeredClientId, principalName);
    }
}
