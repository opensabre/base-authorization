package io.github.opensabre.authorization.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import io.github.opensabre.authorization.entity.User;
import io.github.opensabre.authorization.provider.OrganizationProvider;
import io.github.opensabre.authorization.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    /**
     * cache prefix key
     */
    private static final String CACHE_PREFIX_KEY = "user:";

    @Resource
    private OrganizationProvider organizationProvider;

    @Override
    @Cached(name = CACHE_PREFIX_KEY, key = "#uniqueId", cacheType = CacheType.BOTH)
    public User getByUniqueId(String uniqueId) {
        return organizationProvider.getUserByUniqueId(uniqueId).getData();
    }
}
