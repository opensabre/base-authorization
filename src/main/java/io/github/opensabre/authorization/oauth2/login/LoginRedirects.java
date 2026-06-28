package io.github.opensabre.authorization.oauth2.login;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

public final class LoginRedirects {

    private LoginRedirects() {
    }

    public static String failureUrl(HttpServletRequest request, String username, String error) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(request.getContextPath() + "/login")
                .queryParam("error", error);
        if (username != null && !username.isBlank()) {
            builder.queryParam("username", username);
        }
        return builder.encode(StandardCharsets.UTF_8).build().toUriString();
    }
}
