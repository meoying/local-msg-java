package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.entity.LocalMessage;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    public Message find(Long id, MessageStatus... messageStatuses) {
        List<Integer> statusList = Arrays.stream(messageStatuses)
                .map(MessageStatus::getCode)
                .collect(Collectors.toList());
        LocalMessage localMessage = repository.find(id, statusList);
        return convert(localMessage);
    }

    @Override
    public Long save(Message message) {
        LocalMessage convert = convert(message);
        convert.setDataChgTime(System.currentTimeMillis());
        convert.setStatus(MessageStatus.Init.getCode());
        convert.setRetryCount(0);
        LocalMessage save = repository.save(convert);

        return save.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateStatusSuccess(Message message, MessageStatus newStatus) {
        return repository.updateStatusSuccess(message.id(), newStatus.getCode(), MessageStatus.Init.getCode(),
                System.currentTimeMillis());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateRetryCount(Message message, MessageStatus newStatus) {
        return repository.updateRetryCount(message.id(), MessageStatus.Init.getCode(), System.currentTimeMillis());
    }

    @Override
    public List<Message> findMessageByPageSize(int pageSize, int pageNum, int maxRetryCount, Long delayTimeStamp) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("dataChgTime").descending());
        Page<LocalMessage> messageByPageSize = repository.findMessageByPageSize(maxRetryCount,
               delayTimeStamp, pageable);
        if (messageByPageSize != null && !messageByPageSize.isEmpty()) {
            return messageByPageSize.stream().map(this::convert).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int failLocalMessage(int maxRetryCount) {
        return repository.failLocalMessage(maxRetryCount, System.currentTimeMillis());
    }


    private Message convert(LocalMessage localMessage) {
        if (localMessage == null) {
            return null;
        }
        return new Message(localMessage.getId(), localMessage.getMsg(), localMessage.getTopic());
    }

    private LocalMessage convert(Message message) {
        LocalMessage localMessage = new LocalMessage();
        localMessage.setId(message.id());
        localMessage.setMsg(message.msg());
        localMessage.setTopic(message.topic());
        return localMessage;
    }

}
