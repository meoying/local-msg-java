package com.meoying.localmessage.simple;

import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.sharding.table.TableNameRouter;
import com.meoying.localmessage.utils.TransactionHelper;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.StopFunc;
import com.meoying.localmessage.v4.api.sharding.MsgTable;
import com.meoying.localmessage.v4.api.sharding.ShardingFuncThreadLocal;
import com.meoying.localmessage.v4.core.exception.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ShardingLocalMessageManager extends DefaultLocalMessageManager {

    private final Logger logger = LoggerFactory.getLogger(ShardingLocalMessageManager.class);

    private final TableNameRouter tableNameRouter;

    public ShardingLocalMessageManager(LocalMessageRepository localMessageRepository,
                                       TransactionHelper transactionHelper, MsgSender msgSender,
                                       TableNameRouter tableNameRouter) {
        super(localMessageRepository, transactionHelper, msgSender);
        this.tableNameRouter = tableNameRouter;
    }

    @Override
    public StopFunc fixMessage() {

        Map<String, List<String>> tableNameMap = tableNameRouter.getTableNameMap();

        AtomicInteger count = new AtomicInteger();
        tableNameMap.forEach((k, v) -> count.addAndGet(v.size()));

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(count.get());
        executor.setMaxPoolSize(count.get());
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix(getClass() + "td-");
        executor.initialize();

        StopFunc stopFunc = new StopFunc(() -> {
            executor.shutdown();
            return null;
        });

        for (Map.Entry<String, List<String>> stringListEntry : tableNameMap.entrySet()) {
            for (String s : stringListEntry.getValue()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        ShardingFuncThreadLocal.set(() -> new MsgTable() {
                            @Override
                            public String getDbName() {
                                return stringListEntry.getKey();
                            }

                            @Override
                            public String getTableName() {
                                return s;
                            }
                        });
                        int pageSize = 100;
                        int pageNum = 0;
                        int maxRetryCount = 3;
                        int errCount = 0;
                        long delayTimeStamp = System.currentTimeMillis() - 5000L;
                        while (!stopFunc.isDone()) {
                            try {
                                //1739516603044
                                //1739521484459
                                List<Message> messageList = localMessageRepository.findMessageByPageSize(pageSize,
                                        pageNum,
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
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw e;
                    } finally {
                        ShardingFuncThreadLocal.remove();
                    }
                }, executor);
            }
        }


        return stopFunc;
    }

}
