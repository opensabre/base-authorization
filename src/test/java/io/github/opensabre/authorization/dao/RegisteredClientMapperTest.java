package io.github.opensabre.authorization.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import javax.annotation.Resource;
import java.time.Duration;
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

    @Test
    void testSelectOneByClientId() {
        QueryWrapper<RegisteredClientPo> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("client_id", "test_client1");
        //执行
        RegisteredClientPo registeredClientPo = registeredClientMapper.selectOne(queryWrapper);
        //验证
        assertEquals("test", registeredClientPo.getClientName());
    }

    @Test
    void testUpdateById() {
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
        registeredClientPo.setTokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(1L)).build().getSettings());
        registeredClientPo.setClientAuthenticationMethods("client_secret_basic");
        //执行
        int result = registeredClientMapper.updateById(registeredClientPo);
        //验证
        assertEquals(1, result);
    }
}