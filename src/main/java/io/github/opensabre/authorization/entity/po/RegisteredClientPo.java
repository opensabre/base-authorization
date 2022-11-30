package io.github.opensabre.authorization.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.opensabre.common.web.entity.po.BasePo;
import lombok.*;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("oauth2_registered_client")
public class RegisteredClientPo extends BasePo {
    private String clientId;
    private Date clientIdIssuedAt;
    private String clientSecret;
    private Date clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
//    @TableField(typeHandler = JacksonTypeHandler.class)
    private String clientSettings;
    private String tokenSettings;
    @TableLogic
    private String deleted = "N";
}
