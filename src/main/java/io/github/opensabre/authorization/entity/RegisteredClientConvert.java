package io.github.opensabre.authorization.entity;

import com.google.common.collect.Sets;
import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.authorization.entity.vo.RegisteredClientVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames.Token.*;

@Component
public class RegisteredClientConvert {

    /**
     * RegisteredClientPo转换为RegisteredClient
     *
     * @param registeredClientPo PO对象
     * @return RegisteredClient
     */
    public RegisteredClient convertToRegisteredClient(RegisteredClientPo registeredClientPo) {
        // 构建scope
        Set<String> scopes = StringUtils.isBlank(registeredClientPo.getScopes())
                ? Set.of()
                : Arrays.stream(StringUtils.split(registeredClientPo.getScopes(), ",")).collect(Collectors.toSet());
        // 构建scope
        Set<String> redirectUris = StringUtils.isBlank(registeredClientPo.getRedirectUris())
                ? Set.of()
                : Arrays.stream(StringUtils.split(registeredClientPo.getRedirectUris(), ",")).collect(Collectors.toSet());
        // 构建gantType
        Set<AuthorizationGrantType> grantTypes = StringUtils.isBlank(registeredClientPo.getAuthorizationGrantTypes())
                ? Set.of()
                : Arrays.stream(StringUtils.split(registeredClientPo.getAuthorizationGrantTypes(), ","))
                .map(AuthorizationGrantType::new)
                .collect(Collectors.toSet());
        // 构建method
        Set<ClientAuthenticationMethod> methods = StringUtils.isBlank(registeredClientPo.getClientAuthenticationMethods())
                ? Set.of()
                : Arrays.stream(StringUtils.split(registeredClientPo.getClientAuthenticationMethods(), ","))
                .map(ClientAuthenticationMethod::new)
                .collect(Collectors.toSet());
        Map<String, Object> tokenSettings = registeredClientPo.getTokenSettings();
        // 构建 RegisteredClient对象
        RegisteredClient.Builder registeredClientBuilder = RegisteredClient.withId(registeredClientPo.getId())
                .clientId(registeredClientPo.getClientId())
                .clientSecret(registeredClientPo.getClientSecret())
                .clientName(registeredClientPo.getClientName())
                .clientSecretExpiresAt(registeredClientPo.getClientSecretExpiresAt() == null ? null : registeredClientPo.getClientSecretExpiresAt().toInstant())
                .redirectUris(uris -> uris.addAll(redirectUris))
                .clientAuthenticationMethods(methodSet -> methodSet.addAll(methods))
                .scopes(scopeSet -> scopeSet.addAll(scopes))
                .authorizationGrantTypes(grantType -> grantType.addAll(grantTypes))
                .clientSettings(ClientSettings.withSettings(registeredClientPo.getClientSettings()).build());
        Object accessTokenTimeToLive = tokenSettings == null ? null : tokenSettings.get(ACCESS_TOKEN_TIME_TO_LIVE);
        Object refreshTokenTimeToLive = tokenSettings == null ? null : tokenSettings.get(REFRESH_TOKEN_TIME_TO_LIVE);
        Object reuseRefreshTokens = tokenSettings == null ? null : tokenSettings.get(REUSE_REFRESH_TOKENS);
        TokenSettings.Builder tokenSettingsBuilder = TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256);
        if (accessTokenTimeToLive instanceof Number number) {
            tokenSettingsBuilder.accessTokenTimeToLive(Duration.ofSeconds(number.longValue()));
        } else {
            tokenSettingsBuilder.accessTokenTimeToLive(Duration.ofSeconds(300));
        }
        if (refreshTokenTimeToLive instanceof Number number) {
            tokenSettingsBuilder.refreshTokenTimeToLive(Duration.ofSeconds(number.longValue()));
        } else {
            tokenSettingsBuilder.refreshTokenTimeToLive(Duration.ofSeconds(3600));
        }
        tokenSettingsBuilder.reuseRefreshTokens(reuseRefreshTokens instanceof Boolean booleanValue ? booleanValue : true);
        registeredClientBuilder.tokenSettings(tokenSettingsBuilder.build());
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
        if (registeredClientForm.getClientSecretExpires() != null) {
            registeredClientPo.setClientSecretExpiresAt(Date.from(Instant.now().plusSeconds(registeredClientForm.getClientSecretExpires())));
        }
        registeredClientPo.setAuthorizationGrantTypes(String.join(",", registeredClientForm.getGrantTypes()));
        registeredClientPo.setClientAuthenticationMethods(StringUtils.join(registeredClientForm.getClientAuthenticationMethods(), ","));
        registeredClientPo.setScopes(String.join(",", registeredClientForm.getScopes()));
        registeredClientPo.setClientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build().getSettings());
        Long accessTokenTimeToLive = registeredClientForm.getAccessTokenTimeToLive() == null ? 300L : registeredClientForm.getAccessTokenTimeToLive();
        Long refreshTokenTimeToLive = registeredClientForm.getRefreshTokenTimeToLive() == null ? 3600L : registeredClientForm.getRefreshTokenTimeToLive();
        registeredClientPo.setTokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(accessTokenTimeToLive))
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .reuseRefreshTokens(true)
                .refreshTokenTimeToLive(Duration.ofSeconds(refreshTokenTimeToLive))
                .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
                .build().getSettings());
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
        registeredClientVo.setClientSecret(registeredClientPo.getClientSecret());
        registeredClientVo.setCreatedBy(registeredClientPo.getCreatedBy());
        registeredClientVo.setCreatedTime(registeredClientPo.getCreatedTime());
        registeredClientVo.setUpdatedBy(registeredClientPo.getUpdatedBy());
        registeredClientVo.setUpdatedTime(registeredClientPo.getUpdatedTime());
        registeredClientVo.setClientIdIssuedAt(registeredClientPo.getClientIdIssuedAt());
        registeredClientVo.setClientSecretExpiresAt(registeredClientPo.getClientSecretExpiresAt());
        registeredClientVo.setScopes(Sets.newHashSet(StringUtils.split(registeredClientPo.getScopes(), ",")));
        registeredClientVo.setRedirectUris(Sets.newHashSet(StringUtils.split(registeredClientPo.getRedirectUris(), ",")));
        registeredClientVo.setAuthorizationGrantTypes(Sets.newHashSet(StringUtils.split(registeredClientPo.getAuthorizationGrantTypes(), ",")));
        registeredClientVo.setClientAuthenticationMethods(Sets.newHashSet(StringUtils.split(registeredClientPo.getClientAuthenticationMethods(), ",")));
        Map<String, Object> tokenSettings = registeredClientPo.getTokenSettings();
        if (tokenSettings != null) {
            Object accessTokenTimeToLive = tokenSettings.get(ACCESS_TOKEN_TIME_TO_LIVE);
            if (accessTokenTimeToLive instanceof Duration duration) {
                registeredClientVo.setAccessTokenTimeToLive(duration.toSeconds());
            } else if (accessTokenTimeToLive instanceof Number number) {
                registeredClientVo.setAccessTokenTimeToLive(number.longValue());
            }
            Object refreshTokenTimeToLive = tokenSettings.get(REFRESH_TOKEN_TIME_TO_LIVE);
            if (refreshTokenTimeToLive instanceof Duration duration) {
                registeredClientVo.setRefreshTokenTimeToLive(duration.toSeconds());
            } else if (refreshTokenTimeToLive instanceof Number number) {
                registeredClientVo.setRefreshTokenTimeToLive(number.longValue());
            }
        }
        return registeredClientVo;
    }

}
