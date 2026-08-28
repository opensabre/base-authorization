package io.github.opensabre.authorization.oauth2.login;

import io.github.opensabre.authorization.online.OnlineUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 注销认证服务会话时，同步删除共享 Redis 中的 gateway 会话。
 *
 * <p>网关不承载注销业务；认证服务通过现有在线用户服务清理网关 Session。</p>
 */
@Component
@RequiredArgsConstructor
public class GatewaySessionLogoutHandler implements LogoutHandler {

    private final OnlineUserService onlineUserService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String sessionId = request.getRequestedSessionId();
        if (StringUtils.hasText(sessionId)) {
            onlineUserService.kickout(sessionId);
        }
        SecurityContextHolder.clearContext();
    }
}
