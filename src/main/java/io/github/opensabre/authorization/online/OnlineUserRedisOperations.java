package io.github.opensabre.authorization.online;

import java.util.Map;
import java.util.Set;

public interface OnlineUserRedisOperations {

    Set<String> members(String key);

    Map<Object, Object> entries(String key);

    void remove(String key, String value);

    void delete(String key);
}
