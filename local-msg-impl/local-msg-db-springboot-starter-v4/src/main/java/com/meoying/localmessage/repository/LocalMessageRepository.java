package com.meoying.localmessage.repository;


import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.MessageStatus;

import java.util.List;

public interface LocalMessageRepository {

    Message find(Long id, MessageStatus... messageStatuses);

    Long save(Message message);

    int updateStatusSuccess(Message message, MessageStatus newStatus);

    int updateRetryCount(Message message, MessageStatus status);

    List<Message> findMessageByPageSize(int pageSize, int pageNum, int maxRetryCount ,Long delayTimeStamp);

    int failLocalMessage(int maxRetryCount);
}
