package io.github.opensabre.authorization.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTypeHandler.setObjectMapper(objectMapper);
    }
    private String clientId;
    private Date clientIdIssuedAt;
    private String clientSecret;
    private Date clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> clientSettings;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> tokenSettings;
    @TableLogic
    private String deleted = "N";
}
