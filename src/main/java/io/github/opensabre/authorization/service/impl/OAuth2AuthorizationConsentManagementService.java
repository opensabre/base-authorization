package io.github.opensabre.authorization.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.dao.OAuth2AuthorizationConsentRepository;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationConsentQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationConsentVo;
import io.github.opensabre.authorization.service.IOAuth2AuthorizationConsentManagementService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;

/**
 * 复用 Spring Authorization Server 服务管理客户端授权同意记录。
 */
@Service
public class OAuth2AuthorizationConsentManagementService
        implements IOAuth2AuthorizationConsentManagementService {

    private final OAuth2AuthorizationConsentRepository repository;
    private final OAuth2AuthorizationConsentService consentService;

    public OAuth2AuthorizationConsentManagementService(
            OAuth2AuthorizationConsentRepository repository,
            OAuth2AuthorizationConsentService consentService) {
        this.repository = repository;
        this.consentService = consentService;
    }

    @Override
    public IPage<OAuth2AuthorizationConsentVo> query(
            Page<?> page, OAuth2AuthorizationConsentQueryParam param) {
        return repository.query(page, param);
    }

    @Override
    public OAuth2AuthorizationConsentVo get(String registeredClientId, String principalName) {
        return repository.findById(registeredClientId, principalName).orElse(null);
    }

    @Override
    public boolean remove(String registeredClientId, String principalName) {
        OAuth2AuthorizationConsent consent =
                consentService.findById(registeredClientId, principalName);
        if (consent == null) {
            return false;
        }
        consentService.remove(consent);
        return true;
    }
}
