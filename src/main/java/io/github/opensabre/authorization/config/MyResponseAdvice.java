package io.github.opensabre.authorization.config;

import io.github.opensabre.common.web.rest.ResponseAdvice;
import org.springframework.context.annotation.Configuration;

/**
 * 如果引入了swagger或knife4j的文档生成组件，这里需要仅扫描自己项目的包，否则文档无法正常生成
 */
@Configuration
public class MyResponseAdvice extends ResponseAdvice {

}