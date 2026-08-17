package io.github.opensabre.authorization.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class WebSecurityConfigTest {

    @Autowired(required = false)
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Autowired(required = false)
    private UserDetailsService userDetailsService;

    @Test
    void shouldRestoreAuthoritiesFromJwtRolesClaim() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("admin")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("roles", List.of("ADMIN", "IT"))
                .build();

        var authentication = new WebSecurityConfig()
                .jwtAuthenticationConverter()
                .convert(jwt);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .contains("ADMIN", "IT");
    }

    @Test
    void shouldConfigureDaoAuthenticationProvider() {
        // Verify that DaoAuthenticationProvider is configured
        assertThat(daoAuthenticationProvider).isNotNull();
    }

    @Test
    void shouldConfigureAuthenticationManager() {
        // Verify that AuthenticationManager is configured
        assertThat(authenticationManager).isNotNull();
    }

    @Test
    void shouldConfigureUserDetailsService() {
        // Verify that UserDetailsService is configured
        assertThat(userDetailsService).isNotNull();
    }
}
