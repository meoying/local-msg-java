package com.meoying.localmessage.simple;

import com.meoying.localmessage.api.LocalMessageManager;
import com.meoying.localmessage.api.Message;
import com.meoying.localmessage.api.MessageStatus;
import com.meoying.localmessage.api.StopFunc;
import com.meoying.localmessage.core.Result;
import com.meoying.localmessage.core.exception.MessageException;
import com.meoying.localmessage.core.exception.NoSuchMessageException;
import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class DefaultLocalMessageManager implements LocalMessageManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultLocalMessageManager.class);
    private final LocalMessageRepository localMessageRepository;
    private final MsgSender msgSender;

    public DefaultLocalMessageManager(LocalMessageRepository localMessageRepository, MsgSender msgSender) {
        this.localMessageRepository = localMessageRepository;
        this.msgSender = msgSender;
    }

    @Override
    public void send(Message message) throws MessageException {
        try {
            Message localMessage = localMessageRepository.find(message.id(), MessageStatus.Init);
            if (localMessage == null) {
                throw new NoSuchMessageException("can't find current message in storage");
            }
            Result<?> send = msgSender.send(message.topic(), message.msg());

            if (send.isSuccess()) {
                if (localMessageRepository.updateStatusSuccess(message, MessageStatus.Success) == 0) {
                    logger.warn("update message sending Success result fail");
                }
            } else {
                if (localMessageRepository.updateRetryCount(message, MessageStatus.Init) == 0) {
                    logger.warn("update message sending Init result fail");
                }
            }
        } catch (Exception e) {
            throw new MessageException("unknown error : ", e);
        }
    }

    @Override
    public Long save(Message message) throws MessageException {
        try {
            return localMessageRepository.save(message);
        } catch (Exception e) {
            throw new MessageException("unknown error : ", e);
        }
    }

    @Override
    public StopFunc fixMessage() {
        StopFunc stopFunc = new StopFunc();
        CompletableFuture.runAsync(() -> {
            int pageSize = 100;
            int pageNum = 0;
            int maxRetryCount = 3;

            while (stopFunc.isDone()) {
                long delayTimeStamp = System.currentTimeMillis() - 5000;
                List<Message> messageList = localMessageRepository.findMessageByPageSize(pageSize, pageNum,
                        maxRetryCount, delayTimeStamp);
                if (messageList.isEmpty()) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ignore) {
                        logger.error(ignore.getMessage());
                    }
                    localMessageRepository.failLocalMessage(maxRetryCount);
                    continue;
                }

                for (Message message : messageList) {
                    if (!stopFunc.isDone()) {
                        logger.info("任务退出");
                        return;
                    }
                    try {
                        send(message);
                    } catch (MessageException e) {
                        logger.warn("send message failed{}, message:{}", e.getMessage(), message, e);
                    }

                }
                pageNum++;
            }
        });
        return stopFunc;
    }


}
