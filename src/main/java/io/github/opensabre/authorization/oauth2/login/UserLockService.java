package io.github.opensabre.authorization.oauth2.login;

public interface UserLockService {

    void lock(String username);
}
