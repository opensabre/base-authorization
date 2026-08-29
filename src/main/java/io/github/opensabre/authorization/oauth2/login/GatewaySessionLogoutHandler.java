package io.github.opensabre.authorization.oauth2.login;

import io.github.opensabre.authorization.online.OnlineUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
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

    private static final String GATEWAY_SESSION_COOKIE = "SESSION";

    private final OnlineUserService onlineUserService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // The gateway uses Spring Session's SESSION cookie while this servlet
        // application may expose its own JSESSIONID. Prefer the forwarded
        // gateway cookie so the OAuth2 client context is actually removed.
        String sessionId = gatewaySessionId(request);
        if (!StringUtils.hasText(sessionId)) {
            sessionId = request.getRequestedSessionId();
        }
        if (StringUtils.hasText(sessionId)) {
            onlineUserService.kickout(sessionId);
        }
        SecurityContextHolder.clearContext();
    }

    private String gatewaySessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (GATEWAY_SESSION_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
