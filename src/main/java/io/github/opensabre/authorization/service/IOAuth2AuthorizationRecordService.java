package io.github.opensabre.authorization.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import io.github.opensabre.authorization.entity.vo.OAuth2AuthorizationRecordVo;

public interface IOAuth2AuthorizationRecordService {

    IPage<OAuth2AuthorizationRecordVo> query(Page<?> page, OAuth2AuthorizationQueryParam param);

    OAuth2AuthorizationRecordVo get(String id);

    /**
     * 删除服务端授权聚合并阻止刷新令牌继续使用。
     * 自包含访问令牌不依赖该聚合校验，将持续有效至自身过期。
     *
     * @param id OAuth2授权记录ID
     * @return 记录存在并已删除时返回true
     */
    boolean revoke(String id);

    /**
     * 删除所有授权材料均已过期的OAuth2授权记录。
     *
     * @return 删除的授权记录数
     */
    int cleanupExpired();
}
