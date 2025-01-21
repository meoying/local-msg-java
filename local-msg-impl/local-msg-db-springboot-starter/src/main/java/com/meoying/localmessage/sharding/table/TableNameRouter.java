package com.meoying.localmessage.sharding.table;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TableNameRouter {

    private final Map<String, List<String>> tableNameMap;

    public TableNameRouter(Map<String, List<String>> tableNameMap) {
        if(Objects.isNull(tableNameMap)){
            throw new IllegalArgumentException("tableNameMap can not be null");
        }
        this.tableNameMap = tableNameMap;
    }

    public Map<String, List<String>> getTableNameMap() {
        return Collections.unmodifiableMap(tableNameMap);
    }
}
