package com.meoying.localmessage;

import com.meoying.localmessage.repository.entity.TbBiz;
import com.meoying.localmessage.repository.jpa.TbBizRepository;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.StopFunc;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest(classes = DemoTestV4.class)
@EnableAutoConfiguration
@EntityScan({"com.meoying.localmessage.repository.entity"})
@EnableJpaRepositories({"com.meoying.localmessage.repository.jpa"})
public class DemoTestV4 {

    private final Logger logger = LoggerFactory.getLogger(DemoTestV4.class);

    @Autowired
    LocalMessageManager localMessageManager;

    @Autowired
    TbBizRepository tbBizRepository;

    @Test
    public void testBiz() {

        localMessageManager.doWithLocalMessage(() -> {
            logger.info("biz start");
            int i = 9;
            return () -> new Message("msg", "topic");
        });


        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBiz1() {

        localMessageManager.doWithLocalMessage(() -> {
            logger.info("biz start");
            throw new RuntimeException("业务问题");
        });


        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBiz2() {

        localMessageManager.doWithLocalMessage(() -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("saasasda");
            tbBizRepository.save(entity);
            return () -> new Message("topic", "saasasda");
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFixMessage() {
        StopFunc stopFunc = localMessageManager.fixMessage();

        try {
            Thread.sleep(1000000L);
            stopFunc.done();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testBiz3() {

        localMessageManager.doWithLocalMessage(() -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("saasasda");
            tbBizRepository.save(entity);
            int i = 1 / 0;
            return () -> new Message("topic", "saasasda");
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
