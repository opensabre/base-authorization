package io.github.opensabre.authorization.entity.vo;

import lombok.Data;

/**
 * 用户授予 OAuth2 客户端的权限记录。
 */
@Data
public class OAuth2AuthorizationConsentVo {

    private String registeredClientId;
    private String clientId;
    private String clientName;
    private String principalName;
    private String authorities;
}
