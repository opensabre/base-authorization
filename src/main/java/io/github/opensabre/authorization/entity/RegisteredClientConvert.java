package io.github.opensabre.authorization.entity;

import com.alibaba.nacos.shaded.com.google.common.collect.Sets;
import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.authorization.entity.vo.RegisteredClientVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

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
        RegisteredClient.Builder registeredClientBuilder = RegisteredClient.withId(registeredClientPo.getId())
                .clientId(registeredClientPo.getClientId())
                .clientSecret(registeredClientPo.getClientSecret())
                .clientSecretExpiresAt(registeredClientPo.getClientSecretExpiresAt().toInstant())
                .clientAuthenticationMethod(new ClientAuthenticationMethod(registeredClientPo.getClientAuthenticationMethods()))
                .redirectUri(registeredClientPo.getRedirectUris())
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
        //设置scope
        Arrays.stream(StringUtils.split(registeredClientPo.getScopes(), ","))
                .forEach(registeredClientBuilder::scope);
        //设置gantType
        Arrays.stream(StringUtils.split(registeredClientPo.getAuthorizationGrantTypes(), ","))
                .forEach(grantType -> {
                    registeredClientBuilder.authorizationGrantType(new AuthorizationGrantType(grantType));
                });
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
