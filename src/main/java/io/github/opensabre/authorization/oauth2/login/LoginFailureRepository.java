package io.github.opensabre.authorization.oauth2.login;

public interface LoginFailureRepository {

    int increment(String username);

    int getCount(String username);

    void reset(String username);
}
