package com.meoying.localmessage.simple;

import com.meoying.localmessage.api.LocalMessageManagerV1;
import com.meoying.localmessage.api.Message;
import com.meoying.localmessage.api.MessageStatus;
import com.meoying.localmessage.core.Result;
import com.meoying.localmessage.core.logging.LogFactory;
import com.meoying.localmessage.core.logging.Logger;
import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class DefaultLocalMessageManager implements LocalMessageManagerV1 {

    private final Logger logger = LogFactory.getLogger(DefaultLocalMessageManager.class);
    private final LocalMessageRepository localMessageRepository;
    private final MsgSender msgSender;

    public DefaultLocalMessageManager(LocalMessageRepository localMessageRepository, MsgSender msgSender) {
        this.localMessageRepository = localMessageRepository;
        this.msgSender = msgSender;
    }

    @Override
    public Result<?> send(Message message) {
        try {
            Message localMessage = localMessageRepository.find(message.id(), MessageStatus.Init,
                    MessageStatus.RetryIng);
            if (localMessage == null) {
                return Result.Fail("-1", "消息不存在");
            }
            Result<?> send = msgSender.send(message.topic(), message.msg());
            //todo
            if (send.isSuccess()) {
                if (localMessageRepository.updateStatusSuccess(message, MessageStatus.Success) == 0) {
                    return Result.Success("发送成功，更新失败", null);
                }
                return Result.Success("发送成功", null);
            } else {
                if (localMessageRepository.updateStatusRetry(message, MessageStatus.RetryIng) == 0) {
                    logger.warn("更新失败...");
                }
            }
            return send;
        } catch (Exception e) {
            return Result.Fail("-1", "发送失败:" + e.getMessage());
        }
    }

    @Override
    public Result<String> save(Message message) {
        try {
            return localMessageRepository.save(message);
        } catch (Exception e) {
            return Result.Fail("-1", "保存失败:" + e.getMessage());
        }
    }

    @Override
    public Result<AtomicBoolean> fixMessage() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        CompletableFuture.runAsync(() -> {
            int pageSize = 100;
            int pageNum = 0;
            int maxRetryCount = 3;
            while (atomicBoolean.get()) {
                List<Message> messageList = localMessageRepository.findMessageByPageSize(pageSize, pageNum, maxRetryCount);
                if (messageList.isEmpty()) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException ignore) {
                        logger.error(ignore.getMessage());
                    }
                    localMessageRepository.failLocalMessage(maxRetryCount);
                    continue;
                }

                for (Message message : messageList) {
                    if (!atomicBoolean.get()) {
                        logger.info("任务退出");
                        return;
                    }
                    Result<?> send = this.send(message);
                    if (!send.isSuccess()) {
                        logger.warn("消息发送失败，{}", send.getMsg());
                    }
                }
                pageNum++;
            }
        });
        return Result.Success("启动成功", atomicBoolean);
    }


}
