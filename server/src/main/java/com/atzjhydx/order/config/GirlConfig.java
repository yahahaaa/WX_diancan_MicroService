package com.atzjhydx.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther LeeMZ
 * @Date 2021/1/30
 **/
@Data
@Component
@ConfigurationProperties(prefix = "girl")
public class GirlConfig {

    private String name;

    private Integer age;
}
