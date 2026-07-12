package io.github.opensabre.authorization.online;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class StringRedisOnlineUserOperations implements OnlineUserRedisOperations {

    private final StringRedisTemplate redisTemplate;

    public StringRedisOnlineUserOperations(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Set<String> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Map<Object, Object> entries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void remove(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
