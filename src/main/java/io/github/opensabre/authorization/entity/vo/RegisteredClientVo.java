package io.github.opensabre.authorization.entity.vo;

import io.github.opensabre.common.web.entity.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisteredClientVo extends BaseVo {
    private String id;
    private String clientId;
    private String clientName;
    private String clientSecret;
    private String createdBy;
    private Date createdTime;
    private String updatedBy;
    private Date updatedTime;
    private Date clientIdIssuedAt;
    private Date clientSecretExpiresAt;
    private Set<String> clientAuthenticationMethods;
    private Set<String> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private Long accessTokenTimeToLive;
    private Long refreshTokenTimeToLive;
}
