package io.github.opensabre.authorization.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import io.github.opensabre.authorization.entity.Role;
import io.github.opensabre.authorization.provider.OrganizationProvider;
import io.github.opensabre.authorization.service.IRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleService implements IRoleService {
    @Resource
    private OrganizationProvider organizationProvider;

    @Override
    @Cached(name = "role4user:", key = "#userId", cacheType = CacheType.BOTH)
    public Set<Role> queryUserRolesByUserId(String userId) {
        return organizationProvider.queryRolesByUserId(userId).getData();
    }
}