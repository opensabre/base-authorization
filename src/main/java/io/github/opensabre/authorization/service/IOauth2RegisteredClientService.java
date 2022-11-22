package io.github.opensabre.authorization.service;

import io.github.opensabre.authorization.entity.form.RegisteredClientForm;

public interface IOauth2RegisteredClientService {
    void save(RegisteredClientForm registeredClientForm);
}
