package io.github.opensabre.authorization.rest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.opensabre.authorization.entity.RegisteredClientConvert;
import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.form.RegisteredClientQueryForm;
import io.github.opensabre.authorization.entity.param.RegisteredClientQueryParam;
import io.github.opensabre.authorization.entity.vo.RegisteredClientVo;
import io.github.opensabre.authorization.service.IOauth2RegisteredClientService;
import io.github.opensabre.common.core.entity.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/client")
@Tag(name = "client")
@Slf4j
public class RegisteredClientController {

    @Resource
    IOauth2RegisteredClientService oauth2RegisteredClientService;

    @Resource
    RegisteredClientConvert registeredClientConvert;

    @Operation(summary = "新增客户端", description = "新增客户端client")
    @PostMapping
    public Boolean add(@Parameter(description = "新增客户端client表单", required = true) @Valid @RequestBody RegisteredClientForm registeredClientForm) {
        log.info("disable with clientId:{}", registeredClientForm.getClientId());
        return oauth2RegisteredClientService.add(registeredClientConvert.convertToRegisteredClientPo(registeredClientForm));
    }

    @Operation(summary = "删除客户端", description = "根据url的id来指定删除对象，逻辑删除")
    @DeleteMapping(value = "/{id}")
    public Boolean disable(@Parameter(name = "id", description = "客户端ID", required = true) @PathVariable String id) {
        log.info("disable with id:{}", id);
        return oauth2RegisteredClientService.disable(id);
    }

    @Operation(summary = "修改客户端", description = "修改指定客户端信息")
    @PutMapping(value = "/{id}")
    public Boolean update(@Parameter(description = "客户端ID", required = true) @PathVariable String id,
                          @Parameter(description = "客户端实体", required = true) @Valid @RequestBody RegisteredClientForm registeredClientForm) {
        log.info("update with id:{}", id);
        return oauth2RegisteredClientService.update(registeredClientConvert.convertToRegisteredClientPo(registeredClientForm));
    }

    @Operation(summary = "获取客户端", description = "获取指定客户端信息")
    @GetMapping(value = "/{id}")
    public RegisteredClientVo get(@Parameter(name = "id", description = "客户端ID", required = true) @PathVariable String id) {
        log.info("get with id:{}", id);
        return registeredClientConvert.convertToRegisteredClientVo(oauth2RegisteredClientService.get(id));
    }

    @Operation(summary = "根据clientId获客户端信息", description = "根据clientId获客户端信息")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "处理成功", content = @Content(schema = @Schema(implementation = Result.class)))
    )
    @GetMapping
    public RegisteredClientVo query(@Parameter(description = "客户端clientId", required = true) @RequestParam String clientId) {
        log.info("query with clientId:{}", clientId);
        return registeredClientConvert.convertToRegisteredClientVo(oauth2RegisteredClientService.getByClientId(clientId));
    }

    @Operation(summary = "搜索客户端", description = "根据条件查询客户端信息")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "处理成功", content = @Content(schema = @Schema(implementation = Result.class)))
    )
    @PostMapping(value = "/conditions")
    public IPage<RegisteredClientVo> search(@Parameter(description = "客户端查询参数", required = true) @Valid @RequestBody RegisteredClientQueryForm registeredClientQueryForm) {
        log.info("search with registeredClientQueryForm:{}", registeredClientQueryForm);
        return oauth2RegisteredClientService.query(registeredClientQueryForm.getPage(), registeredClientQueryForm.toParam(RegisteredClientQueryParam.class));
    }
}
