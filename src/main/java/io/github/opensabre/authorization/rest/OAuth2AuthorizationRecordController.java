package io.github.opensabre.authorization.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.authorization.entity.form.OAuth2AuthorizationQueryForm;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationRecordVo;
import io.github.opensabre.authorization.service.IOAuth2AuthorizationRecordService;
import io.github.opensabre.governance.audit.annotations.Audit;
import io.github.opensabre.governance.audit.annotations.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorizations")
@Tag(name = "OAuth2授权记录")
public class OAuth2AuthorizationRecordController {

    private final IOAuth2AuthorizationRecordService authorizationRecordService;

    public OAuth2AuthorizationRecordController(IOAuth2AuthorizationRecordService authorizationRecordService) {
        this.authorizationRecordService = authorizationRecordService;
    }

    @Operation(summary = "分页查询OAuth2授权记录")
    @PostMapping("/conditions")
    public IPage<OAuth2AuthorizationRecordVo> search(@Valid @RequestBody OAuth2AuthorizationQueryForm form) {
        return authorizationRecordService.query(
                form.getPage(), form.toParam(OAuth2AuthorizationQueryParam.class));
    }

    @Operation(summary = "查看OAuth2授权记录")
    @GetMapping("/{id}")
    public OAuth2AuthorizationRecordVo get(
            @Parameter(description = "授权记录ID", required = true) @PathVariable String id) {
        return authorizationRecordService.get(id);
    }

    @Operation(
            summary = "终止OAuth2服务端授权",
            description = "删除服务端授权记录并阻止Refresh Token继续使用；已签发的自包含JWT Access Token仍有效至过期")
    @DeleteMapping("/{id}")
    @Audit(
            operationType = OperationType.DELETE,
            description = "终止OAuth2服务端授权",
            module = "OAUTH2_AUTHORIZATION",
            response = true,
            key = "#id")
    public Boolean revoke(
            @Parameter(description = "授权记录ID", required = true) @PathVariable String id) {
        return authorizationRecordService.revoke(id);
    }
}
