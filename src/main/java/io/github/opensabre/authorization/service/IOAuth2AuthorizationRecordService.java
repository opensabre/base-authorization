package io.github.opensabre.authorization.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationRecordVo;

public interface IOAuth2AuthorizationRecordService {

    IPage<OAuth2AuthorizationRecordVo> query(Page<?> page, OAuth2AuthorizationQueryParam param);

    OAuth2AuthorizationRecordVo get(String id);

    boolean revoke(String id);
}
