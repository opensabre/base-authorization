package io.github.opensabre.authorization.config;

import io.github.opensabre.authorization.oauth2.device.DeviceClientAuthenticationConverter;
import io.github.opensabre.authorization.oauth2.device.DeviceClientAuthenticationProvider;
import io.github.opensabre.authorization.oauth2.handler.Oauth2AccessDeniedHandler;
import io.github.opensabre.authorization.oauth2.handler.Oauth2DeviceSuccessHandler;
import io.github.opensabre.authorization.oauth2.handler.Oauth2FailureHandler;
import io.github.opensabre.authorization.oauth2.handler.Oauth2SuccessHandler;
import io.github.opensabre.authorization.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.function.Function;

import static cn.hutool.core.bean.BeanUtil.beanToMap;

@Slf4j
@Configuration
public class AuthorizationServerConfig {

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
    private static final String CUSTOM_VERIFICATION_URI = "/oauth2/activate";
    private static final String CUSTOM_LOGIN_FORM_URL = "/login";

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
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity httpSecurity,
                                                                      RegisteredClientRepository registeredClientRepository,
                                                                      AuthorizationServerSettings authorizationServerSettings) throws Exception {
        log.info("Init HttpSecurity for Oauth2");
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        // 自定义用户映射器
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            return new OidcUserInfo(beanToMap(userService.getByUniqueId(principal.getName())));
        };
        Oauth2FailureHandler errorResponseHandler = new Oauth2FailureHandler();
        // 新建设备码converter和provider
        DeviceClientAuthenticationConverter deviceClientAuthenticationConverter =
                new DeviceClientAuthenticationConverter(authorizationServerSettings.getDeviceAuthorizationEndpoint());
        DeviceClientAuthenticationProvider deviceClientAuthenticationProvider = new DeviceClientAuthenticationProvider(registeredClientRepository);

        httpSecurity
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // 设置客户端授权中失败的handler处理
                .clientAuthentication((auth) -> auth.errorResponseHandler(errorResponseHandler))
                // token 相关配置 如:/oauth2/token接口
                .tokenEndpoint((token) -> token.errorResponseHandler(errorResponseHandler))
                // Enable OpenID Connect 1.0
                .oidc((oidc) -> {
                    // userinfo返回自定义用户信息
                    oidc.userInfoEndpoint((userInfo) -> {
                                userInfo.userInfoMapper(userInfoMapper);
                                userInfo.userInfoResponseHandler(new Oauth2SuccessHandler());
                            }
                    );
                })
                // 设置自定义用户确认授权页
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI))
                // 设置设备码用户验证url(自定义用户验证页)
                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint -> deviceAuthorizationEndpoint.verificationUri(CUSTOM_VERIFICATION_URI))
                // 设置验证设备码用户确认授权页
                .deviceVerificationEndpoint(deviceVerificationEndpoint -> {
                    deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI);
                    deviceVerificationEndpoint.deviceVerificationResponseHandler(new Oauth2DeviceSuccessHandler());
                })
                // 客户端认证添加设备码的converter和provider
                .clientAuthentication(clientAuthentication ->
                        clientAuthentication
                                .authenticationConverter(deviceClientAuthenticationConverter)
                                .authenticationProvider(deviceClientAuthenticationProvider)
                );
        // 未通过身份验证异常时重定向到登录页面授权端点（通过浏览器访问时）
        httpSecurity.exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint(CUSTOM_LOGIN_FORM_URL),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        ));
        // 处理使用access token访问用户信息端点和客户端注册端点
        httpSecurity.oauth2ResourceServer(resourceServer ->
                resourceServer
                        .jwt(Customizer.withDefaults())
                        .accessDeniedHandler(new Oauth2AccessDeniedHandler())
        );
        return httpSecurity.build();
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