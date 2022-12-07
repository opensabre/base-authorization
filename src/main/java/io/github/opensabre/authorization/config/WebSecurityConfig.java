package io.github.opensabre.authorization.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Resource
    UserDetailsService userDetailsService;

    /**
     * 用于身份验证的 Spring Security 过滤器链
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Init HttpSecurity for Security");
        httpSecurity.csrf().disable();
        httpSecurity.authorizeRequests()
                .antMatchers("/client", "/client/**").permitAll()
                .anyRequest().authenticated();
        //表单登录处理从授权服务器过滤器链
        httpSecurity.formLogin(Customizer.withDefaults())
                .userDetailsService(userDetailsService);
        return httpSecurity.build();
    }
}