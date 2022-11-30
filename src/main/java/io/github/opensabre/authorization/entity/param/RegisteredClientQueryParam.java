package io.github.opensabre.authorization.entity.param;

import io.github.opensabre.authorization.entity.RegisteredClientConvert;
import io.github.opensabre.authorization.entity.User;
import io.github.opensabre.authorization.entity.po.RegisteredClientPo;
import io.github.opensabre.common.web.entity.form.BaseQueryForm;
import io.github.opensabre.common.web.entity.param.BaseParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredClientQueryParam extends BaseParam<RegisteredClientPo> {
    /**
     * clientId oauth2客户端id
     */
    private String clientId;
    /**
     * clientName oauth2客户端名称
     */
    private String clientName;
}
