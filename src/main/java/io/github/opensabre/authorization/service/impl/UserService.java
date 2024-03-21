package io.github.opensabre.authorization.service.impl;

import io.github.opensabre.authorization.entity.User;
import io.github.opensabre.authorization.provider.OrganizationProvider;
import io.github.opensabre.authorization.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Resource
    private OrganizationProvider organizationProvider;

    @Override
    public User getByUniqueId(String uniqueId) {
        return organizationProvider.getUserByUniqueId(uniqueId).getData();
    }
}
