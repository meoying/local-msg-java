package com.meoying.localmessage.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.meoying.localmessage")
public class localMessageProperties {

    private String loggerClass = "com.meoying.localmessage.logging.Slf4jLoggerImpl";

    private String defaultDataSourceName ="defaultDataSource";

    public String getLoggerClass() {
        return loggerClass;
    }

    public String getDefaultDataSourceName() {
        return defaultDataSourceName;
    }

    public void setDefaultDataSourceName(String defaultDataSourceName) {
        this.defaultDataSourceName = defaultDataSourceName;
    }

    public void setLoggerClass(String loggerClass) {
        this.loggerClass = loggerClass;
    }
}
