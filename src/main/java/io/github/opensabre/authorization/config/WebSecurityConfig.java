package io.github.opensabre.authorization.config;

import jakarta.annotation.Resource;
import io.github.opensabre.authorization.oauth2.login.LoginAuthenticationFailureHandler;
import io.github.opensabre.authorization.oauth2.login.LoginAuthenticationSuccessHandler;
import io.github.opensabre.authorization.oauth2.login.LoginCaptchaAuthenticationFilter;
import io.github.opensabre.authorization.oauth2.login.LogoutAuditSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;


@Slf4j
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    @Resource
    UserDetailsService userDetailsService;
    @Resource
    private LoginCaptchaAuthenticationFilter loginCaptchaAuthenticationFilter;
    @Resource
    private LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler;
    @Resource
    private LoginAuthenticationFailureHandler loginAuthenticationFailureHandler;
    @Resource
    private LogoutAuditSuccessHandler logoutAuditSuccessHandler;

    /**
     * 用于身份验证的 Spring Security 过滤器链
     *
     * @param httpSecurity Spring Security 过滤器链
     * @return SecurityFilterChain
     * @throws Exception Security
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity httpSecurity,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        log.info("Init HttpSecurity for Security");
        // web站点基本安全配置
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        // url安全配置
        httpSecurity.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers("/doc.html", "/v3/**", "/webjars/**", "/assets/**", "/favicon.svg", "/login")
                        .permitAll()
                        .requestMatchers("/login/captcha/image")
                        .permitAll()
                        .requestMatchers("/actuator/internalTokenKeyStatus")
                        .permitAll()
                        .requestMatchers(
                                "/authorizations", "/authorizations/**",
                                "/authorization-consents", "/authorization-consents/**")
                        .hasAnyAuthority("ADMIN", "IT")
                        .requestMatchers("/client", "/client/**", "/online-users", "/online-users/**", "/oauth2/activate*", "/oauth2/consent", "/", "/profile")
                        .authenticated());
        // 表单登录处理从授权服务器过滤器链
        httpSecurity
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .successHandler(loginAuthenticationSuccessHandler)
                .failureHandler(loginAuthenticationFailureHandler))
                .addFilterBefore(loginCaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService);
        // 管理台以 DELETE /logout 发起注销，成功后由处理器写入审计日志并返回 204。
        httpSecurity.logout(logout -> logout
                .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.DELETE, "/logout"))
                .logoutSuccessHandler(logoutAuditSuccessHandler));
        // 添加BearerTokenAuthenticationFilter，将认证服务当做一个资源服务，解析请求头中的token
        httpSecurity.oauth2ResourceServer((resourceServer) -> resourceServer
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return httpSecurity.build();
    }

    /**
     * 从 JWT roles 声明恢复应用角色，保证直连授权服务时也执行管理角色校验。
     *
     * @return JWT 认证转换器
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }
}
