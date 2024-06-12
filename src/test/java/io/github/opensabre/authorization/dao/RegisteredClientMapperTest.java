package io.github.opensabre.authorization.dao;

import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RegisteredClientMapperTest {

    @Resource
    private RegisteredClientMapper registeredClientMapper;

    @Test
    void testInsert() {
        RegisteredClientPo registeredClientPo = new RegisteredClientPo();
        registeredClientPo.setClientId("test_client1");
        registeredClientPo.setClientSecret("123456");
        registeredClientPo.setClientName("test");
        registeredClientPo.setClientSecretExpiresAt(Date.from(Instant.now()));
        registeredClientPo.setScopes("read");
        registeredClientPo.setRedirectUris("https://github.com");
        registeredClientPo.setClientIdIssuedAt(Date.from(Instant.now()));
        registeredClientPo.setAuthorizationGrantTypes("client_credentials");
        registeredClientPo.setClientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build().getSettings());
        registeredClientPo.setTokenSettings(TokenSettings.builder().build().getSettings());
        registeredClientPo.setClientAuthenticationMethods("client_secret_basic");
        //执行
        int result = registeredClientMapper.insert(registeredClientPo);
        //验证
        assertEquals(1, result);
    }
}