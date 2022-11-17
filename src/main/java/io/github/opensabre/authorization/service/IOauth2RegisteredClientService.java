package io.github.opensabre.authorization.service;

import io.github.opensabre.authorization.entity.form.RegisteredClientForm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

public interface IOauth2RegisteredClientService {
    void save(RegisteredClientForm registeredClientForm);
}
