package io.github.opensabre.authorization.service;

import io.github.opensabre.authorization.entity.Role;

import java.util.Set;

public interface IRoleService {
    Set<Role> queryUserRolesByUserId(String userId);
}
