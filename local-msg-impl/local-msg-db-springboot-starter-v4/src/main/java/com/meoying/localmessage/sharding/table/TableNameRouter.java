package com.meoying.localmessage.sharding.table;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TableNameRouter {

    private final Map<String, List<String>> tableNameMap;

    public TableNameRouter(Map<String, List<String>> tableNameMap) {
        if (Objects.isNull(tableNameMap)) {
            throw new IllegalArgumentException("tableNameMap can not be null");
        }

        this.tableNameMap = tableNameMap.entrySet().stream()
                .filter(e -> !e.getKey().trim().isEmpty())
                .collect(Collectors.toMap(
                        e -> e.getKey().trim(),
                        e -> e.getValue().stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList())
                ));

    }

    public Map<String, List<String>> getTableNameMap() {
        return Collections.unmodifiableMap(tableNameMap);
    }
}
