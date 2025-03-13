package com.meoying.localmessage.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.meoying.localmessage")
@Getter
@Setter
public class LocalMessageProperties {

    private Boolean enable = false;
    private String type = "simple";

}