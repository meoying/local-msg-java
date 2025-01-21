package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.api.MessageStatus;
import com.meoying.localmessage.repository.entity.LocalMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalMessageDao extends JpaRepository<LocalMessage, Long> {

//    @Query("SELECT m FROM #{#tableName} m WHERE m.id = :id AND m.status IN :statuses")
//    LocalMessage find(@Param("tableName") String tableName, @Param("id") String id, @Param("statuses")
//    List<MessageStatus> messageStatuses);

    @Query("SELECT m FROM LocalMessage m WHERE m.id = :id AND m.status IN :statuses")
    LocalMessage find(@Param("id") long id, @Param("statuses") List<Integer> messageStatuses);

    @Query("UPDATE LocalMessage m SET m.status = :newStatus, m.dataChgTime = CURRENT_TIMESTAMP WHERE m.id = :id AND " +
            "m" + ".status = :oldStatuses")
    @Modifying
    int updateStatusSuccess(@Param("id") long id, @Param("newStatus") int newStatus,
                            @Param("oldStatuses") int oldStatus);

    @Query("UPDATE LocalMessage m SET m.retryCount = m.retryCount + 1, m.dataChgTime = CURRENT_TIMESTAMP WHERE m.id " +
            "=" + " :id AND m.status = :status")
    @Modifying
    int updateRetryCount(@Param("id") long id, @Param("status") int status);

    @Query("SELECT m FROM LocalMessage m WHERE m.retryCount <= :maxRetryCount AND m.dataChgTime < :timestamp ORDER " +
            "BY" + " m.dataChgTime ASC")
    Page<LocalMessage> findMessageByPageSize(@Param("maxRetryCount") int maxRetryCount,
                                             @Param("timestamp") java.sql.Timestamp timestamp, Pageable pageable);

    @Query("UPDATE LocalMessage m SET m.status = '3', m.dataChgTime = CURRENT_TIMESTAMP WHERE m.retryCount > " +
            ":maxRetryCount and m.status != 1")
    @Modifying
    int failLocalMessage(@Param("maxRetryCount") int maxRetryCount);
}