package io.github.opensabre.authorization.entity;

import com.google.common.collect.Sets;
import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.authorization.entity.vo.RegisteredClientVo;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RegisteredClientConvert {

    @Resource
    PasswordEncoder passwordEncoder;

    /**
     * RegisteredClientPo转换为RegisteredClient
     *
     * @param registeredClientPo PO对象
     * @return RegisteredClient
     */
    public RegisteredClient convertToRegisteredClient(RegisteredClientPo registeredClientPo) {
        // 构建scope
        Set<String> scopes = Arrays.stream(StringUtils.split(registeredClientPo.getScopes(), ","))
                .collect(Collectors.toSet());
        // 构建scope
        Set<String> redirectUris = Arrays.stream(StringUtils.split(registeredClientPo.getRedirectUris(), ","))
                .collect(Collectors.toSet());
        // 构建gantType
        Set<AuthorizationGrantType> grantTypes = Arrays.stream(StringUtils.split(registeredClientPo.getAuthorizationGrantTypes(), ","))
                .map(AuthorizationGrantType::new)
                .collect(Collectors.toSet());
        // 构建method
        Set<ClientAuthenticationMethod> methods = Arrays.stream(StringUtils.split(registeredClientPo.getClientAuthenticationMethods(), ","))
                .map(ClientAuthenticationMethod::new)
                .collect(Collectors.toSet());
        // 构建 RegisteredClient对象
        RegisteredClient.Builder registeredClientBuilder = RegisteredClient.withId(registeredClientPo.getId())
                .clientId(registeredClientPo.getClientId())
                .clientSecret(registeredClientPo.getClientSecret())
                .clientName(registeredClientPo.getClientName())
                .clientSecretExpiresAt(registeredClientPo.getClientSecretExpiresAt().toInstant())
                .redirectUris(uris -> uris.addAll(redirectUris))
                .clientAuthenticationMethods(methodSet -> methodSet.addAll(methods))
                .scopes(scopeSet -> scopeSet.addAll(scopes))
                .authorizationGrantTypes(grantType -> grantType.addAll(grantTypes))
                .clientSettings(ClientSettings.withSettings(registeredClientPo.getClientSettings()).build())
                .tokenSettings(TokenSettings.builder()
                                // token有效期5小时
//                        .accessTokenTimeToLive(registeredClientPo.getTokenSettings().getAccessTokenTimeToLive())
                                // 使用默认JWT相关格式
                                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                                // 开启刷新token
                                .reuseRefreshTokens(true)
                                // refreshToken有效期120分钟
//                        .refreshTokenTimeToLive(registeredClientPo.getTokenSettings().getRefreshTokenTimeToLive())
                                // idToken签名算法
                                .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256).build()
                );
        return registeredClientBuilder.build();
    }

    /**
     * 将RegisteredClientForm转为RegisteredClientPo，方便Dao存储
     *
     * @param registeredClientForm RegisteredClient对象实例
     * @return RegisteredClientPo实例
     */
    public RegisteredClientPo convertToRegisteredClientPo(RegisteredClientForm registeredClientForm) {
        RegisteredClientPo registeredClientPo = new RegisteredClientPo();
        registeredClientPo.setId(registeredClientForm.getId());
        registeredClientPo.setClientId(registeredClientForm.getClientId());
        registeredClientPo.setClientName(registeredClientForm.getClientName());
        registeredClientPo.setClientSecret(registeredClientForm.getClientSecret());
        registeredClientPo.setRedirectUris(registeredClientForm.getRedirectUri());
        registeredClientPo.setClientSecretExpiresAt(Date.from(Instant.now().plusSeconds(registeredClientForm.getClientSecretExpires())));
        registeredClientPo.setAuthorizationGrantTypes(String.join(",", registeredClientForm.getGrantTypes()));
        registeredClientPo.setClientAuthenticationMethods(String.join(",", registeredClientForm.getClientAuthenticationMethods()));
        registeredClientPo.setScopes(String.join(",", registeredClientForm.getScopes()));
        registeredClientPo.setClientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build().getSettings());
        registeredClientPo.setTokenSettings(TokenSettings.builder().build().getSettings());
        return registeredClientPo;
    }

    /**
     * 将RegisteredClientPo转为RegisteredClientVo，前端展示
     *
     * @param registeredClientPo RegisteredClientPo对象实例
     * @return RegisteredClientVo实例
     */
    public RegisteredClientVo convertToRegisteredClientVo(RegisteredClientPo registeredClientPo) {
        RegisteredClientVo registeredClientVo = new RegisteredClientVo();
        registeredClientVo.setId(registeredClientPo.getId());
        registeredClientVo.setClientId(registeredClientPo.getClientId());
        registeredClientVo.setClientName(registeredClientPo.getClientName());
        registeredClientVo.setClientIdIssuedAt(registeredClientPo.getClientIdIssuedAt());
        registeredClientVo.setClientSecretExpiresAt(registeredClientPo.getClientSecretExpiresAt());
        registeredClientVo.setScopes(Sets.newHashSet(StringUtils.split(registeredClientPo.getScopes(), ",")));
        registeredClientVo.setRedirectUris(Sets.newHashSet(StringUtils.split(registeredClientPo.getRedirectUris(), ",")));
        registeredClientVo.setAuthorizationGrantTypes(Sets.newHashSet(StringUtils.split(registeredClientPo.getAuthorizationGrantTypes(), ",")));
        registeredClientVo.setClientAuthenticationMethods(Sets.newHashSet(StringUtils.split(registeredClientPo.getClientAuthenticationMethods(), ",")));
        return registeredClientVo;
    }

}
