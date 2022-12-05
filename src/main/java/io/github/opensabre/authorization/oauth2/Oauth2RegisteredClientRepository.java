package io.github.opensabre.authorization.oauth2;

import io.github.opensabre.authorization.entity.RegisteredClientConvert;
import io.github.opensabre.authorization.service.IOauth2RegisteredClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class Oauth2RegisteredClientRepository implements RegisteredClientRepository {

    @Resource
    IOauth2RegisteredClientService oauth2RegisteredClientService;

    @Resource
    RegisteredClientConvert registeredClientConvert;

    @Deprecated
    @Override
    public void save(RegisteredClient registeredClient) {
        log.warn("请使用IOauth2RegisteredClientService相关方法，该实现废弃！");
    }

    @Override
    public RegisteredClient findById(String id) {
        return registeredClientConvert.convertToRegisteredClient(oauth2RegisteredClientService.get(id));
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return registeredClientConvert.convertToRegisteredClient(oauth2RegisteredClientService.getByClientId(clientId));
    }

}
