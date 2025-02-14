package com.meoying.localmessage.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "com.meoying.localmessage")
public class LocalMessageProperties {

    private Boolean enable = false;
    private String type = "simple";

    private String defaultDataSourceName = "defaultDataSource";
    private String defaultTableName = "local_message";

    //@NestedConfigurationProperty
    private Map<String, List<String>> tableNameMap;

    public String getDefaultDataSourceName() {
        return defaultDataSourceName;
    }

    public void setDefaultDataSourceName(String defaultDataSourceName) {
        this.defaultDataSourceName = defaultDataSourceName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Map<String, List<String>> getTableNameMap() {
        return tableNameMap;
    }

    public void setTableNameMap(Map<String, List<String>> tableNameMap) {
        this.tableNameMap = tableNameMap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultTableName() {
        return defaultTableName;
    }

    public void setDefaultTableName(String defaultTableName) {
        this.defaultTableName = defaultTableName;
    }
}