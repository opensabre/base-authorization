package io.github.opensabre.authorization.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OAuth2AuthorizationRecordVo {

    private String id;
    private String clientId;
    private String clientName;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String accessTokenType;
    private Date accessTokenIssuedAt;
    private Date accessTokenExpiresAt;
    private Date refreshTokenIssuedAt;
    private Date refreshTokenExpiresAt;
    private boolean hasIdToken;
    private boolean hasDeviceCode;
    private String status;
}
