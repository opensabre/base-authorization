package io.github.opensabre.authorization.config;

import cn.hutool.core.bean.BeanUtil;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.opensabre.authorization.oauth2.handler.Oauth2AccessDeniedHandler;
import io.github.opensabre.authorization.oauth2.handler.Oauth2FailureHandler;
import io.github.opensabre.authorization.oauth2.Oauth2RegisteredClientRepository;
import io.github.opensabre.authorization.oauth2.handler.Oauth2SuccessHandler;
import io.github.opensabre.authorization.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AuthorizationServerConfig {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    @Resource
    private IUserService userService;

    /**
     * 端点的 Spring Security 过滤器链
     *
     * @param httpSecurity Spring Security 过滤器链
     * @return SecurityFilterChain
     * @throws Exception 初使化异常
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Init HttpSecurity for Oauth2");
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        // 自定义用户映射器
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            return new OidcUserInfo(BeanUtil.beanToMap(userService.getByUniqueId(principal.getName())));
        };
        httpSecurity
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // 设置客户端授权中失败的handler处理
                .clientAuthentication((auth) -> auth.errorResponseHandler(new Oauth2FailureHandler()))
                // token 相关配置 如:/oauth2/token接口
                .tokenEndpoint((token) -> token.errorResponseHandler(new Oauth2FailureHandler()))
                // Enable OpenID Connect 1.0
                .oidc((oidc) -> {
                    // /userinfo返回自定义用户信息
                    oidc.userInfoEndpoint((userInfo) -> {
                                userInfo.userInfoMapper(userInfoMapper);
                                userInfo.userInfoResponseHandler(new Oauth2SuccessHandler());
                            }
                    );
                });
        // 未通过身份验证异常时重定向到登录页面授权端点
        httpSecurity.exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        ));
        //
        httpSecurity.oauth2ResourceServer((resourceServer) -> resourceServer
                .jwt(Customizer.withDefaults())
                .accessDeniedHandler(new Oauth2AccessDeniedHandler())
        );
        return httpSecurity.build();
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
     * @return token生成器
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
     * @return token自定义
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwtClaimsSet.Builder claims = context.getClaims();
            Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                    .stream()
                    .map(c -> c.replaceFirst("^ROLE_", ""))
                    .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
            claims.claim("roles", roles);
            log.info("context:{}", context);
        };
    }

    /**
     * 操作oauth2_authorization表，token等相关信息表
     *
     * @return OAuth2AuthorizationService
     */
    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, oauth2RegisteredClientRepository);
    }

    /**
     * 授权确认信息处理服务，操作oauth2_authorization_consent表，权限相关表。
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, oauth2RegisteredClientRepository);
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
     * AuthorizationServerSettings配置 Spring Authorization Server的实例
     *
     * @return AuthorizationServerSettings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}