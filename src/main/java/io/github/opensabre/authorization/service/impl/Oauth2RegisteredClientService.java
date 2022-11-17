package io.github.opensabre.authorization.service.impl;

import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.service.IOauth2RegisteredClientService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class Oauth2RegisteredClientService implements IOauth2RegisteredClientService {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    PasswordEncoder passwordEncoder;

    JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(this.jdbcTemplate);

    private RegisteredClient convertToRegisteredClient(RegisteredClientForm registeredClientForm) {
        RegisteredClient.Builder registeredClientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(registeredClientForm.getClientId())
                .clientSecret(passwordEncoder.encode(registeredClientForm.getClientSecret()))
                .clientSecretExpiresAt(Instant.now().plusSeconds(registeredClientForm.getClientSecretExpires()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(registeredClientForm.getRedirectUri())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        // token有效期5小时
                        .accessTokenTimeToLive(Duration.ofHours(registeredClientForm.getAccessTokenTimeToLive()))
                        // 使用默认JWT相关格式
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        // 开启刷新token
                        .reuseRefreshTokens(true)
                        // refreshToken有效期120分钟
                        .refreshTokenTimeToLive(Duration.ofDays(registeredClientForm.getRefreshTokenTimeToLive()))
                        // idToken签名算法
                        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256).build()
                );
        //设置scope
        registeredClientBuilder.scopes(build -> registeredClientForm.getScopes());
        //设置gantType
        registeredClientForm.getGrantTypes().forEach(grantType -> {
            registeredClientBuilder.authorizationGrantType(new AuthorizationGrantType(grantType));
        });
        return registeredClientBuilder.build();
    }

    @Override
    public void save(RegisteredClientForm registeredClientForm) {
        Optional<RegisteredClient> clientOptional = Optional.ofNullable(jdbcRegisteredClientRepository.findByClientId(registeredClientForm.getClientId()));
        if (clientOptional.isEmpty()) {
            jdbcRegisteredClientRepository.save(convertToRegisteredClient(registeredClientForm));
        }
    }
}
