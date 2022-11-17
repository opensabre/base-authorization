package io.github.opensabre.authorization.rest;

import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.service.IOauth2RegisteredClientService;
import io.github.opensabre.common.core.entity.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/client")
@Tag(name = "client")
@Slf4j
public class RegisteredClientController {

    @Resource
    IOauth2RegisteredClientService oauth2RegisteredClientService;

    @Operation(summary = "新增客户端", description = "新增客户端client")
    @PostMapping
    public Result save(@Parameter(description = "新增客户端client表单", required = true) RegisteredClientForm registeredClientForm) {
        oauth2RegisteredClientService.save(registeredClientForm);
        return Result.success();
    }
}
