package com.meoying.localmessage.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.meoying.loaclmessage")
public class localMessageProperties {

    private String loggerClass = "com.meoying.localmessage.logging.Slf4jLoggerImpl";

    public String getLoggerClass() {
        return loggerClass;
    }
}
