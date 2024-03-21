package io.github.opensabre.authorization.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    @Resource
    UserDetailsService userDetailsService;

    /**
     * 用于身份验证的 Spring Security 过滤器链
     *
     * @param httpSecurity
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Init HttpSecurity for Security");
        //
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        //
        httpSecurity.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers("/doc.html", "/v3/**", "/webjars/**", "/assets/**")
                        .permitAll()
                        .requestMatchers("/client", "/client/**")
                        .authenticated());
        //表单登录处理从授权服务器过滤器链
        httpSecurity
                .formLogin(Customizer.withDefaults())
                .userDetailsService(userDetailsService);
        return httpSecurity.build();
    }
}