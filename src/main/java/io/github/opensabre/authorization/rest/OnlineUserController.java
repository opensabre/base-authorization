package io.github.opensabre.authorization.rest;

import io.github.opensabre.authorization.online.OnlineUser;
import io.github.opensabre.authorization.online.OnlineUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/online-users")
@Tag(name = "在线用户")
@Slf4j
public class OnlineUserController {

    @Resource
    private OnlineUserService onlineUserService;

    @Operation(summary = "查询在线用户", description = "查询网关共享会话中的在线用户")
    @GetMapping
    public List<OnlineUser> list(@Parameter(description = "用户名") @RequestParam(required = false) String username) {
        log.info("list online users with username:{}", username);
        return onlineUserService.list(username);
    }

    @Operation(summary = "踢出在线用户", description = "删除在线索引与网关共享会话")
    @DeleteMapping("/{sessionId}")
    public Boolean kickout(@Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {
        log.info("kickout online user with sessionId:{}", sessionId);
        return onlineUserService.kickout(sessionId);
    }
}
