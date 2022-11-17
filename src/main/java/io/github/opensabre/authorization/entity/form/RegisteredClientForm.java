package io.github.opensabre.authorization.entity.form;

import io.github.opensabre.common.web.entity.form.BaseForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Schema
@Data
public class RegisteredClientForm extends BaseForm {

    @NotBlank(message = "客户端ID不能为空")
    @Schema(title = "客户端ID")
    String clientId;

    @NotBlank(message = "客户端密码不能为空")
    @Schema(title = "客户端密码")
    String clientSecret;

    @NotEmpty(message = "授权类型不能为空")
    @Schema(title = "授权类型")
    Set<String> grantTypes;

    @NotEmpty(message = "授权范围不能为空")
    @Schema(title = "授权范围")
    Set<String> scopes;

    @NotBlank(message = "回调URL不能为空")
    @Schema(title = "回调URL")
    String redirectUri;

    @Schema(title = "客户端密码过期时间")
    long clientSecretExpires;

    @Schema(title = "accessToken有效期")
    long accessTokenTimeToLive;

    @Schema(title = "refreshToken有效期")
    long refreshTokenTimeToLive;
}
