package io.github.opensabre.authorization.entity.form;

import io.github.opensabre.authorization.entity.param.OAuth2AuthorizationQueryParam;
import io.github.opensabre.persistence.entity.form.BaseQueryForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2AuthorizationQueryForm extends BaseQueryForm<OAuth2AuthorizationQueryParam> {

    private String clientId;
    private String principalName;
    private String authorizationGrantType;
    private String status;
}
