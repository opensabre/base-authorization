package io.github.opensabre.authorization.rest;

import io.github.opensabre.authorization.entity.RegisteredClientConvert;
import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.authorization.service.IOauth2RegisteredClientService;
import io.github.opensabre.common.core.entity.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result add(@Parameter(description = "新增客户端client表单", required = true) @Valid @RequestBody RegisteredClientForm registeredClientForm) {
        oauth2RegisteredClientService.add(registeredClientConvert.convertToRegisteredClientPo(registeredClientForm));
        return Result.success();
    }
}
