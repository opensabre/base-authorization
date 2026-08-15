package io.github.opensabre.authorization.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Jackson3TypeHandler;
import io.github.opensabre.authorization.entity.vo.RegisteredClientVo;
import io.github.opensabre.persistence.entity.po.BasePo;
import lombok.*;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "oauth2_registered_client", autoResultMap = true)
public class RegisteredClientPo extends BasePo<RegisteredClientVo> {
    private String clientId;
    private Date clientIdIssuedAt;
    private String clientSecret;
    private Date clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
    @TableField(typeHandler = Jackson3TypeHandler.class)
    private Map<String, Object> clientSettings;
    @TableField(typeHandler = Jackson3TypeHandler.class)
    private Map<String, Object> tokenSettings;
    @TableLogic
    private String deleted = "N";
}
