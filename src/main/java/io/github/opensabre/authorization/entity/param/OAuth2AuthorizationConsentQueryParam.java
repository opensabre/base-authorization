package io.github.opensabre.authorization.entity.param;

import io.github.opensabre.persistence.entity.param.BaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 客户端授权同意记录查询参数。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2AuthorizationConsentQueryParam extends BaseParam {

    private String clientId;
    private String principalName;
    private String authority;
}
