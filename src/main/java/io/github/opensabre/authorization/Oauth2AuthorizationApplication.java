package io.github.opensabre.authorization;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import io.github.opensabre.governance.audit.annotations.EnabledAudit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnabledAudit
@EnableFeignClients
@EnableMethodCache(basePackages = {"io.github.opensabre.authorization"})
public class Oauth2AuthorizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(Oauth2AuthorizationApplication.class, args);
    }
}
