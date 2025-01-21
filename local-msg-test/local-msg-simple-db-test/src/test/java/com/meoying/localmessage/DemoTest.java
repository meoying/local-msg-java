package com.meoying.localmessage;

import com.meoying.localmessage.api.LocalMessageManager;
import com.meoying.localmessage.api.Message;
import com.meoying.localmessage.api.MessageResHolder;
import com.meoying.localmessage.api.Transaction;
import com.meoying.localmessage.configration.MyApplicationContextInitializer;
import com.meoying.localmessage.domain.DefaultMessage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = DemoTest.class)
@EnableAutoConfiguration
@AutoConfigureDataJdbc
//@ContextConfiguration(initializers = MyApplicationContextInitializer.class)

public class DemoTest {

    private final Logger logger= LoggerFactory.getLogger(DemoTest.class);

    @Autowired
    LocalMessageManager localMessageManager;

    @Autowired
    Transaction Transaction;
    
    @Test
    public void testBiz(){
        
        localMessageManager.accept(Transaction,()->{
            logger.info("biz start");
            int i=9;
            return new MessageResHolder<Integer>(i,new DefaultMessage("topic","msg"));
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
