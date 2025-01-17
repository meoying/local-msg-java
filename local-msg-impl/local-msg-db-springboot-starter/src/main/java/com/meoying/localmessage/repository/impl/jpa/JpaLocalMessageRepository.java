package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.api.Message;
import com.meoying.localmessage.api.MessageStatus;
import com.meoying.localmessage.core.Result;
import com.meoying.localmessage.domain.DefaultMessage;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.entity.LocalMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JpaLocalMessageRepository implements LocalMessageRepository {

    private final LocalMessageDao repository;

    public JpaLocalMessageRepository(LocalMessageDao repository) {
        this.repository = repository;
    }

    @Override
    public Message find(String id, MessageStatus... messageStatuses) {
        List<MessageStatus> statusList = Arrays.asList(messageStatuses);
        LocalMessage localMessage = repository.find(id, statusList);
        return convert(localMessage);
    }

    @Override
    public Result<String> save(Message message) {
        LocalMessage save = repository.save(convert(message));
        return Result.Success("保存成功", save.getId());
    }

    @Override
    public int updateStatusSuccess(Message message, MessageStatus newStatus) {
        return repository.updateStatusSuccess(message.id(), newStatus, Arrays.asList(MessageStatus.Init,
                MessageStatus.RetryIng));
    }

    @Override
    public int updateStatusRetry(Message message, MessageStatus newStatus) {
        return repository.updateStatusRetry(message.id(), newStatus, Arrays.asList(MessageStatus.Init,
                MessageStatus.RetryIng));
    }

    @Override
    public List<Message> findMessageByPageSize(int pageSize, int pageNum, int maxRetryCount) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("dataChgTime").descending());
        List<LocalMessage> messageByPageSize = repository.findMessageByPageSize(pageSize, pageNum, maxRetryCount,
                pageable);
        if (messageByPageSize != null && !messageByPageSize.isEmpty()) {
            return messageByPageSize.stream().map(this::convert).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public int failLocalMessage(int maxRetryCount) {
        return repository.failLocalMessage(maxRetryCount);
    }


    private Message convert(LocalMessage localMessage) {
        return new DefaultMessage(localMessage.getId(), localMessage.getMsg(), localMessage.getTopic());
    }

    private LocalMessage convert(Message message) {
        LocalMessage localMessage = new LocalMessage();
        localMessage.setId(message.id());
        localMessage.setMsg(message.msg());
        localMessage.setTopic(message.topic());
        return localMessage;
    }

}
