package io.github.opensabre.authorization.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.opensabre.authorization.oauth2.JWKCacheManager;
import io.github.opensabre.authorization.oauth2.Oauth2RegisteredClientRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AuthorizationServiceConfig {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private JWKCacheManager jwkCacheManager;

    @Resource
    private Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    @Value("${opensabre.oauth2.issuer-uri:localhost:8000}")
    private String issuerUri;

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
     * 将用户角色放入token中
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
            claims.issuer(issuerUri);
            log.info("context:{}", context.getPrincipal().toString());
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
        // 先从redis获取，解析存储的jws
        JWKSet jwkSet = jwkCacheManager.getJWKSet();
        return new ImmutableJWKSet<>(jwkSet);
    }

}