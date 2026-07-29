package io.github.opensabre.authorization.entity.form;

import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationConsentQueryParam;
import io.github.opensabre.persistence.entity.form.BaseQueryForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 客户端授权同意记录分页查询表单。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2AuthorizationConsentQueryForm
        extends BaseQueryForm<OAuth2AuthorizationConsentQueryParam> {

    private String clientId;
    private String principalName;
    private String authority;
}
