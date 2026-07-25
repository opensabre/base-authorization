package io.github.opensabre.authorization.entity.param;

import io.github.opensabre.persistence.entity.param.BaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OAuth2AuthorizationQueryParam extends BaseParam {

    private String clientId;
    private String principalName;
    private String authorizationGrantType;
    private String status;
}
