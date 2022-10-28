package io.github.opensabre.authorization.service;

import io.github.opensabre.authorization.entity.User;
import org.springframework.cache.annotation.Cacheable;

public interface IUserService {

    /**
     * 根据用户唯一标识获取用户信息
     *
     * @param uniqueId 唯一ID
     * @return 用户信息
     */
    @Cacheable(value = "#id")
    User getByUniqueId(String uniqueId);
}
