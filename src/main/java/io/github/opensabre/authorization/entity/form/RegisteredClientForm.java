package io.github.opensabre.authorization.entity.form;

import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.common.web.entity.form.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Schema
@Data
public class RegisteredClientForm extends BaseForm<RegisteredClientPo> {

    @Schema(title = "唯一ID")
    @NotBlank(message = "更新时ID不能为空", groups = Update.class)
    String id;

    @NotBlank(message = "客户端ID不能为空", groups = Create.class)
    @Schema(title = "客户端ID")
    String clientId;

    @NotBlank(message = "客户端名称不能为空", groups = Create.class)
    @Schema(title = "客户端名称")
    String clientName;

    @NotBlank(message = "客户端密码不能为空", groups = Create.class)
    @Schema(title = "客户端密码")
    String clientSecret;

    @NotEmpty(message = "授权类型不能为空", groups = Create.class)
    @Schema(title = "授权类型")
    Set<String> grantTypes;

    @NotEmpty(message = "客户端认证方法", groups = Create.class)
    @Schema(title = "客户端认证方式")
    Set<String> clientAuthenticationMethods;

    @NotEmpty(message = "授权范围不能为空", groups = Create.class)
    @Schema(title = "授权范围")
    Set<String> scopes;

    @Schema(title = "回调URL，多个信任回调地址以\",\"间隔")
    String redirectUri;

    @Schema(title = "客户端密码过期时间，单位秒")
    Long clientSecretExpires;

    @Schema(title = "accessToken有效期，单位秒")
    Long accessTokenTimeToLive;

    @Schema(title = "refreshToken有效期，单位秒")
    Long refreshTokenTimeToLive;

    @Override
    public RegisteredClientPo toPo(Class<RegisteredClientPo> clazz) {
        RegisteredClientPo registeredClientPo = super.toPo(clazz);
        registeredClientPo.setScopes(getJoinStr(this.scopes));
        registeredClientPo.setAuthorizationGrantTypes(getJoinStr(this.grantTypes));
        registeredClientPo.setClientAuthenticationMethods(getJoinStr(this.clientAuthenticationMethods));
        registeredClientPo.setRedirectUris(String.join(",", this.redirectUri));
        return registeredClientPo;
    }

    /**
     * @param sets
     * @return
     */
    private String getJoinStr(Set<String> sets) {
        return StringUtils.join(sets, ",");
    }

    interface Create {
    }

    interface Update {
    }
}
