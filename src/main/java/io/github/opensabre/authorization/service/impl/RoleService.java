package io.github.opensabre.authorization.service.impl;

import io.github.opensabre.authorization.entity.Role;
import io.github.opensabre.authorization.provider.OrganizationProvider;
import io.github.opensabre.authorization.service.IRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class RoleService implements IRoleService {

    @Resource
    private OrganizationProvider organizationProvider;

    @Override
    public Set<Role> queryUserRolesByUserId(String userId) {
        return organizationProvider.queryRolesByUserId(userId).getData();
    }

}
