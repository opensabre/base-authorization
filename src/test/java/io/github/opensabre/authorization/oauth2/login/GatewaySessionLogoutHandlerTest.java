package io.github.opensabre.authorization.oauth2.login;

import io.github.opensabre.authorization.online.OnlineUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GatewaySessionLogoutHandlerTest {

    @Test
    void removesSharedGatewaySessionFromRequestedSessionId() {
        OnlineUserService onlineUserService = mock(OnlineUserService.class);
        GatewaySessionLogoutHandler handler = new GatewaySessionLogoutHandler(onlineUserService);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestedSessionId()).thenReturn("session-1");

        handler.logout(request, mock(HttpServletResponse.class), null);

        verify(onlineUserService, timeout(1000)).kickout("session-1");
        SecurityContextHolder.clearContext();
    }

    @Test
    void prefersForwardedGatewaySessionCookieOverAuthSessionId() {
        OnlineUserService onlineUserService = mock(OnlineUserService.class);
        GatewaySessionLogoutHandler handler = new GatewaySessionLogoutHandler(onlineUserService);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("JSESSIONID", "auth-session"),
                new Cookie("SESSION", "gateway-session")
        });
        when(request.getRequestedSessionId()).thenReturn("auth-session");

        handler.logout(request, mock(HttpServletResponse.class), null);

        verify(onlineUserService, timeout(1000)).kickout("gateway-session");
        SecurityContextHolder.clearContext();
    }
}
