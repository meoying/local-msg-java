package com.meoying.localmessage.simple;

import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.utils.TransactionHelper;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.MessageStatus;
import com.meoying.localmessage.v4.api.StopFunc;
import com.meoying.localmessage.v4.core.Result;
import com.meoying.localmessage.v4.core.exception.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public class DefaultLocalMessageManager implements LocalMessageManager {

    protected final LocalMessageRepository localMessageRepository;
    private final Logger logger = LoggerFactory.getLogger(DefaultLocalMessageManager.class);
    private final TransactionHelper transactionHelper;
    private final MsgSender msgSender;

    public DefaultLocalMessageManager(LocalMessageRepository localMessageRepository,
                                      TransactionHelper transactionHelper, MsgSender msgSender) {
        this.localMessageRepository = localMessageRepository;
        this.transactionHelper = transactionHelper;
        this.msgSender = msgSender;
    }

    @Override
    public void send(Message message) throws MessageException {
        try {
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
            long delayTimeStamp = System.currentTimeMillis() - 5000;
            int errCount = 0;
            while (!stopFunc.isDone()) {
                try {

                    List<Message> messageList = localMessageRepository.findMessageByPageSize(pageSize, pageNum,
                            maxRetryCount, delayTimeStamp);
                    if (messageList.isEmpty()) {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException ignore) {
                            logger.error(ignore.getMessage());
                        }
                        localMessageRepository.failLocalMessage(maxRetryCount);
                        delayTimeStamp = System.currentTimeMillis() - 5000L;
                        pageNum = 0;
                        errCount = 0;
                        continue;
                    }

                    for (Message message : messageList) {
                        if (stopFunc.isDone()) {
                            logger.info("任务退出");
                            return;
                        }
                        try {
                            send(message);
                        } catch (MessageException e) {
                            logger.warn("send message failed{}, message:{}", e.getMessage(), message, e);
                        }
                    }

                    if (messageList.size() < pageSize) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ignore) {
                            logger.error(ignore.getMessage());
                        }
                        localMessageRepository.failLocalMessage(maxRetryCount);
                        delayTimeStamp = System.currentTimeMillis() - 5000L;
                        pageNum = 0;
                        errCount = 0;
                        continue;
                    }
                    pageNum++;
                    errCount = 0;
                } catch (RuntimeException e) {
                    errCount++;
                    logger.error(e.getMessage(), e);
                    if (errCount > 3) {
                        throw e;
                    }
                }
            }
        });
        return stopFunc;
    }

    @Override
    public <T> T doWithTransaction(Supplier<T> supplier) {
        return transactionHelper.execute(Propagation.REQUIRES_NEW, status -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
        });
    }
}
