package io.github.opensabre.authorization.entity;

import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    @Test
    void testRegisteredClientPoConvertToRegisteredClientVo() {
    }
}