package com.springboot.auth.authorization.service;

import com.springboot.auth.authorization.entity.Role;

import java.util.Set;

public interface IRoleService {
    Set<Role> queryUserRolesByUserId(String userId);
}
