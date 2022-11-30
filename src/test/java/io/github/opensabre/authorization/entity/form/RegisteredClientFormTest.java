package io.github.opensabre.authorization.entity.form;

import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredClientFormTest {

    @Test
    void testPo() {
        RegisteredClientForm registeredClientForm = new RegisteredClientForm();
        registeredClientForm.setClientId("test_client");
        registeredClientForm.setClientSecret("abc123");
        registeredClientForm.setClientSecretExpires(7200L);
        registeredClientForm.setAccessTokenTimeToLive(7200L);
        registeredClientForm.setGrantTypes(Sets.newSet("password","client_credentials"));
        registeredClientForm.setScopes(Sets.newSet("read","write"));
        //执行
        RegisteredClientPo registeredClientPo = registeredClientForm.toPo(RegisteredClientPo.class);
        //验证
        assertEquals("test_client",registeredClientPo.getClientId());
        assertEquals("password,client_credentials",registeredClientPo.getAuthorizationGrantTypes());
        assertEquals("read,write",registeredClientPo.getScopes());
        assertEquals("read,write",registeredClientPo.getClientSecret());
    }
}