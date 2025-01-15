package com.meoying.localmessage.repository;

import com.meoying.localmessage.api.Message;
import com.meoying.localmessage.api.MessageStatus;
import com.meoying.localmessage.core.Result;

import java.util.List;

public interface LocalMessageRepository {

    Message find(String id, MessageStatus... messageStatuses);

    Result<String> save(Message message);

    int updateStatusSuccess(Message message, MessageStatus newStatus);

    int updateStatusRetry(Message message, MessageStatus newStatus);

    List<Message> findMessageByPageSize(int pageSize, int pageNum, int maxRetryCount);

    int failLocalMessage(int maxRetryCount);
}
