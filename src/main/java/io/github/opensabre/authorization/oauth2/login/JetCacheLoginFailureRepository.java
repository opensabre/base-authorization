package io.github.opensabre.authorization.oauth2.login;

import com.alicp.jetcache.AutoReleaseLock;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class JetCacheLoginFailureRepository implements LoginFailureRepository {

    private static final String CACHE_NAME = "auth:login-failure:";
    private static final String LOCK_SUFFIX = ":lock";

    private final CacheManager cacheManager;
    private final LoginSecurityProperties properties;
    private Cache<String, Integer> failureCache;

    @PostConstruct
    public void init() {
        QuickConfig quickConfig = QuickConfig.newBuilder(CACHE_NAME)
                .expire(properties.getFailureExpire())
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .build();
        failureCache = cacheManager.getOrCreateCache(quickConfig);
    }

    @Override
    public int increment(String username) {
        AutoReleaseLock lock = failureCache.tryLock(username + LOCK_SUFFIX, 3, TimeUnit.SECONDS);
        try {
            int count = getCount(username) + 1;
            failureCache.put(username, count, properties.getFailureExpire().toSeconds(), TimeUnit.SECONDS);
            return count;
        } finally {
            if (lock != null) {
                lock.close();
            }
        }
    }

    @Override
    public int getCount(String username) {
        Integer count = failureCache.get(username);
        return count == null ? 0 : count;
    }

    @Override
    public void reset(String username) {
        failureCache.remove(username);
    }
}
