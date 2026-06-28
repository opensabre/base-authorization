package io.github.opensabre.authorization.entity;

import lombok.Data;

@Data
public class CaptchaVo {

    private String captchaId;
    private String imageData;
    private Integer expireTime;
}
