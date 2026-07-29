package io.github.opensabre.authorization.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.dao.OAuth2AuthorizationRecordRepository;
import io.github.opensabre.authorization.config.OAuth2AuthorizationCleanupProperties;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationRecordVo;
import io.github.opensabre.authorization.service.IOAuth2AuthorizationRecordService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OAuth2AuthorizationRecordService implements IOAuth2AuthorizationRecordService {

    private final OAuth2AuthorizationRecordRepository repository;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2AuthorizationCleanupProperties cleanupProperties;

    public OAuth2AuthorizationRecordService(OAuth2AuthorizationRecordRepository repository,
                                            OAuth2AuthorizationService authorizationService,
                                            OAuth2AuthorizationCleanupProperties cleanupProperties) {
        this.repository = repository;
        this.authorizationService = authorizationService;
        this.cleanupProperties = cleanupProperties;
    }

    @Override
    public IPage<OAuth2AuthorizationRecordVo> query(Page<?> page, OAuth2AuthorizationQueryParam param) {
        return repository.query(page, param);
    }

    @Override
    public OAuth2AuthorizationRecordVo get(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public boolean revoke(String id) {
        // Spring Authorization Server删除授权聚合后会拒绝后续刷新；
        // 已签发的自包含JWT不依赖数据库校验，因此仍按自身过期时间失效。
        OAuth2Authorization authorization = authorizationService.findById(id);
        if (authorization == null) {
            return false;
        }
        authorizationService.remove(authorization);
        return true;
    }

    @Override
    public int cleanupExpired() {
        return cleanupExpiredBefore(Instant.now());
    }

    public int cleanupExpiredBefore(Instant cutoff) {
        int total = 0;
        int batchSize = Math.max(1, cleanupProperties.getBatchSize());
        int maxBatches = Math.max(1, cleanupProperties.getMaxBatchesPerRun());
        for (int batch = 0; batch < maxBatches; batch++) {
            int deleted = repository.deleteExpiredBatch(cutoff, batchSize);
            total += deleted;
            if (deleted < batchSize) {
                break;
            }
        }
        return total;
    }
}
