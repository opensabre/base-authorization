package io.github.opensabre.authorization.entity.vo;

import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.common.web.entity.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisteredClientVo extends BaseVo<RegisteredClientPo> {

    private String id;
    private String clientId;
    private Date clientIdIssuedAt;
    private Date clientSecretExpiresAt;
    private String clientName;
    private Set<ClientAuthenticationMethod> clientAuthenticationMethods;
    private Set<AuthorizationGrantType> authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
    private ClientSettings clientSettings;
    private TokenSettings tokenSettings;
}



