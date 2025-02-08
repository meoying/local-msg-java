package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.configuration.LocalMessageProperties;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.entity.LocalMessage;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.MessageStatus;
import com.meoying.localmessage.v4.api.sharding.ShardingFuncThreadLocal;
import com.meoying.localmessage.v4.core.exception.NoSuchRoutingTableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class JpaShardingLocalMessageRepository implements LocalMessageRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaShardingLocalMessageRepository.class);
    private final ShardingLocalMessageDaoCustom repository;
    private final LocalMessageProperties localMessageProperties;

    public JpaShardingLocalMessageRepository(ShardingLocalMessageDaoCustom repository, LocalMessageProperties localMessageProperties) {
        this.repository = repository;
        this.localMessageProperties = localMessageProperties;
    }

    @Override
    public Message find(Long id, MessageStatus... messageStatuses) {
        List<Integer> statusList = Arrays.stream(messageStatuses)
                .map(MessageStatus::getCode)
                .collect(Collectors.toList());
        LocalMessage localMessage = repository.find(getTableName(), id, statusList);
        return convert(localMessage);
    }

    @Override
    public Long save(Message message) {
        LocalMessage convert = convert(message);
        convert.setDataChgTime(System.currentTimeMillis());
        convert.setStatus(MessageStatus.Init.getCode());
        convert.setRetryCount(0);
        LocalMessage save = repository.save(getTableName(), convert);
        return save.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateStatusSuccess(Message message, MessageStatus newStatus) {
        return repository.updateStatusSuccess(getTableName(), message.id(), newStatus.getCode(),
                MessageStatus.Init.getCode());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateRetryCount(Message message, MessageStatus newStatus) {
        return repository.updateRetryCount(getTableName(), message.id(), MessageStatus.Init.getCode());
    }

    @Override
    public List<Message> findMessageByPageSize(int pageSize, int pageNum, int maxRetryCount, Long delayTimeStamp) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("dataChgTime").descending());
        Page<LocalMessage> messageByPageSize = repository.findMessageByPageSize(getTableName(), maxRetryCount,
                delayTimeStamp, pageable);
        if (messageByPageSize != null && !messageByPageSize.isEmpty()) {
            return messageByPageSize.stream().map(this::convert).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int failLocalMessage(int maxRetryCount) {
        return repository.failLocalMessage(getTableName(), maxRetryCount);
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

    private String getTableName() {
        try {
            return ShardingFuncThreadLocal.get().getSharding().getTableName();
        } catch (Exception ignore) {
//            log.error("no such routing table", ignore);
//            throw new NoSuchRoutingTableException("no such routing table");
            return localMessageProperties.getDefaultTableName();
        }
    }

}
