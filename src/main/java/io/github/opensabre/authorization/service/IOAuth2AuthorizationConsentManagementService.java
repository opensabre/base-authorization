package io.github.opensabre.authorization.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationConsentQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationConsentVo;

/**
 * 客户端授权同意记录管理服务。
 */
public interface IOAuth2AuthorizationConsentManagementService {

    IPage<OAuth2AuthorizationConsentVo> query(
            Page<?> page, OAuth2AuthorizationConsentQueryParam param);

    OAuth2AuthorizationConsentVo get(String registeredClientId, String principalName);

    /**
     * 删除授权同意记录；已签发 Token 的生命周期仍由 Token 签发管理负责。
     */
    boolean remove(String registeredClientId, String principalName);
}
