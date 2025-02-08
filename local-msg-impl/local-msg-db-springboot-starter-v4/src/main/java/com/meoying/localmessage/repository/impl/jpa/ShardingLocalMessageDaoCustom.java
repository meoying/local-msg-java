package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.repository.entity.LocalMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface ShardingLocalMessageDaoCustom {
    LocalMessage find(String tableName, long id, List<Integer> messageStatuses);
    int updateStatusSuccess(String tableName, long id, int newStatus, int oldStatus);
    int updateRetryCount(String tableName, long id, int status);
    Page<LocalMessage> findMessageByPageSize(String tableName, int maxRetryCount, long timestamp, Pageable pageable);
    int failLocalMessage(String tableName, int maxRetryCount);
    LocalMessage save(String tableName, LocalMessage message);
}
