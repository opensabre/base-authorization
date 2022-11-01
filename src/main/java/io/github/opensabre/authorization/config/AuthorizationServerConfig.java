package io.github.opensabre.authorization.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {
    @Resource
    JdbcTemplate jdbcTemplate;

    /**
     * 用于密码加密
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 用于jwt解码
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 用于jwt编码
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(jwkSource());
    }

    /**
     * token生成
     *
     * @return
     */
    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator() {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder());
        jwtGenerator.setJwtCustomizer(jwtCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    /**
     * 自定义JWT token内容
     *
     * @return
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwsHeader.Builder headers = context.getHeaders();
            JwtClaimsSet.Builder claims = context.getClaims();
            Map<String, Object> map = claims.build().getClaims();
            if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
                // Customize headers/claims for access_token
                // headers.header("customerHeader", "这是一个自定义header");
                // claims.claim("customerClaim", "这是一个自定义Claim");
                // 取出用户名，查询其他用户信息，放入token claim中
                // String username = (String) map.get("sub");
                // String sql = "select avatar, url from oauth_demo.oauth2_user where username = ?";
                // UserEntity userEntity = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserEntity.class), username);
                // Optional<UserEntity> userEntityOptional = Optional.ofNullable(userEntity);
                // if (userEntityOptional.isPresent()) {
                //     claims.claim("url", userEntityOptional.get().getUrl());
                //     claims.claim("avatar", userEntityOptional.get().getAvatar());
                // }
            }
        };
    }

    /**
     * 端点的 Spring Security 过滤器链
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        //未通过身份验证异常时重定向到登录页面授权端点
        httpSecurity.exceptionHandling(
                (exceptions) -> exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );
        return httpSecurity.build();
    }

    /**
     * RegisteredClientRepository用于管理客户端的实例。操作oauth2_registered_client表
     *
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("test_client")
                .clientSecret(passwordEncoder().encode("test_client"))
                .clientSecretExpiresAt(Instant.now().plusSeconds(7200))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("https://www.baidu.com")
                .scope(OidcScopes.OPENID)
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        // token有效期100分钟
                        .accessTokenTimeToLive(Duration.ofMinutes(100L))
                        // 使用默认JWT相关格式
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        // 开启刷新token
                        .reuseRefreshTokens(true)
                        // refreshToken有效期120分钟
                        .refreshTokenTimeToLive(Duration.ofMinutes(120L))
                        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256).build()
                )
                .build();
        RegisteredClient client = jdbcRegisteredClientRepository.findByClientId("test-client");
        Optional<RegisteredClient> clientOptional = Optional.ofNullable(client);
        if (clientOptional.isEmpty()) {
            jdbcRegisteredClientRepository.save(registeredClient);
        }
        return jdbcRegisteredClientRepository;
    }

    /**
     * 操作oauth2_authorization表，token等相关信息表
     *
     * @return OAuth2AuthorizationService
     */
    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
    }

    /**
     * 授权确认信息处理服务，操作oauth2_authorization_consent表，权限相关表。
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository());
    }

    /**
     * 生成jwk资源,com.nimbusds.jose.jwk.source.JWKSource用于签署访问令牌的实例。
     *
     * @return JWKSource<SecurityContext>
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 生成密钥对,启动时生成的带有密钥的实例java.security.KeyPair用于创建JWKSource上述内容
     *
     * @return KeyPair
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * ProviderSettings配置 Spring Authorization Server的实例
     *
     * @return ProviderSettings
     */
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().build();
    }
}
