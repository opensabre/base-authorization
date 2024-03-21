package io.github.opensabre.authorization.entity.form;

import io.github.opensabre.authorization.entity.param.RegisteredClientQueryParam;
import io.github.opensabre.persistence.entity.form.BaseQueryForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredClientQueryForm extends BaseQueryForm<RegisteredClientQueryParam> {
    /**
     * clientId oauth2客户端id
     */
    private String clientId;
    /**
     * clientName oauth2客户端名称
     */
    private String clientName;
}
