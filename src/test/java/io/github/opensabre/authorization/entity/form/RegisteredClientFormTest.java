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
        registeredClientForm.setRedirectUri("http://localhost:8080");
        registeredClientForm.setClientAuthenticationMethods(Sets.newSet("test"));
        //执行
        RegisteredClientPo registeredClientPo = registeredClientForm.toPo(RegisteredClientPo.class);
        //验证
        assertEquals("test_client",registeredClientPo.getClientId());
        assertEquals("password,client_credentials",registeredClientPo.getAuthorizationGrantTypes());
        assertEquals("read,write",registeredClientPo.getScopes());
        assertEquals("test",registeredClientPo.getClientAuthenticationMethods());
        assertEquals("http://localhost:8080", registeredClientPo.getRedirectUris());
        assertEquals("abc123",registeredClientPo.getClientSecret());
    }

    @Test
    void testPoNullSets() {
        RegisteredClientForm registeredClientForm = new RegisteredClientForm();
        registeredClientForm.setClientId("test_client");
        registeredClientForm.setClientSecret("abc123");
        registeredClientForm.setClientSecretExpires(7200L);
        registeredClientForm.setAccessTokenTimeToLive(7200L);
        registeredClientForm.setGrantTypes(Sets.newSet("password","client_credentials"));
        registeredClientForm.setScopes(Sets.newSet("read"));
        registeredClientForm.setRedirectUri("http://localhost:8080");
        //执行
        RegisteredClientPo registeredClientPo = registeredClientForm.toPo(RegisteredClientPo.class);
        //验证
        assertEquals("test_client",registeredClientPo.getClientId());
        assertEquals("password,client_credentials",registeredClientPo.getAuthorizationGrantTypes());
        assertEquals("read", registeredClientPo.getScopes());
        assertEquals("http://localhost:8080", registeredClientPo.getRedirectUris());
        assertNull(registeredClientPo.getClientAuthenticationMethods());
        assertEquals("abc123",registeredClientPo.getClientSecret());
    }
}