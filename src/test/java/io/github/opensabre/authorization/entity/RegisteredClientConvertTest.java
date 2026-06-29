package io.github.opensabre.authorization.entity;

import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RegisteredClientConvertTest {

    @Resource
    RegisteredClientConvert registeredClientConvert;

    @Test
    void testRegisteredClientPoConvertToRegisteredClient() {
    }

    @Test
    void testRegisteredClientConvertToRegisteredClientPo() {
    }

    @Test
    void testRegisteredClientFormConvertToRegisteredClientPo() {
        RegisteredClientForm registeredClientForm = new RegisteredClientForm();
        registeredClientForm.setClientId("test_client");
        registeredClientForm.setClientSecret("abc123");
        registeredClientForm.setClientName("test");
        registeredClientForm.setClientSecretExpires(7200L);
        registeredClientForm.setAccessTokenTimeToLive(7200L);
        registeredClientForm.setRefreshTokenTimeToLive(38400L);
        registeredClientForm.setGrantTypes(Sets.newSet("password", "client_credentials"));
        registeredClientForm.setScopes(Sets.newSet("read", "write"));
        registeredClientForm.setRedirectUri("https://baidu.com");
        registeredClientForm.setUsername("admin");
        //执行
        RegisteredClientPo registeredClientPo = registeredClientConvert.convertToRegisteredClientPo(registeredClientForm);
        //验证
        assertEquals("password,client_credentials", registeredClientPo.getAuthorizationGrantTypes());
        assertEquals("read,write", registeredClientPo.getScopes());
        assertEquals("https://baidu.com", registeredClientPo.getRedirectUris());
        assertEquals("test", registeredClientPo.getClientName());
        assertNotNull(registeredClientPo.getTokenSettings());
        assertNotNull(registeredClientPo.getTokenSettings().get("settings.token.access-token-time-to-live"));
        assertNotNull(registeredClientPo.getTokenSettings().get("settings.token.refresh-token-time-to-live"));
    }

    @Test
    void testRegisteredClientPoConvertToRegisteredClientVo() {
        RegisteredClientPo registeredClientPo = new RegisteredClientPo();
        registeredClientPo.setId("100");
        registeredClientPo.setClientId("test_client");
        registeredClientPo.setClientName("test");
        registeredClientPo.setClientSecret("abc123");
        registeredClientPo.setAuthorizationGrantTypes("client_credentials,authorization_code");
        registeredClientPo.setScopes("read,write");
        registeredClientPo.setRedirectUris("https://baidu.com");
        registeredClientPo.setClientAuthenticationMethods("client_secret_basic");
        registeredClientPo.setClientSecretExpiresAt(new java.util.Date());
        registeredClientPo.setCreatedBy("admin");
        registeredClientPo.setUpdatedBy("ops");
        registeredClientPo.setCreatedTime(new java.util.Date(1000L));
        registeredClientPo.setUpdatedTime(new java.util.Date(2000L));
        registeredClientPo.setTokenSettings(TokenSettings.builder().accessTokenTimeToLive(java.time.Duration.ofSeconds(7200)).refreshTokenTimeToLive(java.time.Duration.ofSeconds(3600)).build().getSettings());

        var registeredClientVo = registeredClientConvert.convertToRegisteredClientVo(registeredClientPo);

        assertEquals("abc123", registeredClientVo.getClientSecret());
        assertEquals("admin", registeredClientVo.getCreatedBy());
        assertEquals("ops", registeredClientVo.getUpdatedBy());
        assertEquals(new java.util.Date(1000L), registeredClientVo.getCreatedTime());
        assertEquals(new java.util.Date(2000L), registeredClientVo.getUpdatedTime());
        assertEquals(7200L, registeredClientVo.getAccessTokenTimeToLive());
        assertEquals(3600L, registeredClientVo.getRefreshTokenTimeToLive());
    }
}
