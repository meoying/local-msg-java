package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.api.MessageStatus;
import com.meoying.localmessage.repository.entity.LocalMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalMessageDao extends JpaRepository<LocalMessage, String> {

    @Query("SELECT m FROM LocalMessage m WHERE m.id = :id AND m.status IN :statuses")
    LocalMessage find(@Param("id") String id, @Param("statuses") List<MessageStatus> messageStatuses);

    @Query("UPDATE LocalMessage m SET m.status = :newStatus WHERE m.id = :id AND m.status IN :oldStatuses")
    @Modifying
    int updateStatusSuccess(@Param("id") String id, @Param("newStatus") MessageStatus newStatus,
                            @Param("oldStatuses") List<MessageStatus> messageStatuses);

    @Query("UPDATE LocalMessage m SET m.status = :newStatus , m.retryCount = m.retryCount + 1 WHERE m.id = :id AND m" +
            ".status IN :oldStatuses")
    @Modifying
    int updateStatusRetry(@Param("id") String id, @Param("newStatus") MessageStatus newStatus,
                          @Param("oldStatuses") List<MessageStatus> messageStatuses);

    @Query("SELECT m FROM LocalMessage m WHERE m.retryCount <= :maxRetryCount ORDER BY m.dataChgTime DESC")
    List<LocalMessage> findMessageByPageSize(@Param("pageSize") int pageSize, @Param("pageNum") int pageNum, @Param(
            "maxRetryCount") int maxRetryCount, Pageable pageable);

    @Query("UPDATE LocalMessage m SET m.status = '3' WHERE m.retryCount > :maxRetryCount and m.status != '1'")
    @Modifying
    int failLocalMessage(@Param("maxRetryCount") int maxRetryCount);
}