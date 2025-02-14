package com.meoying.localmessage;

import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.JpaLocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.LocalMessageDao;
import com.meoying.localmessage.simple.DefaultLocalMessageManager;
import com.meoying.localmessage.utils.TransactionHelper;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage",
        name = "type", havingValue = "simple"
)
public class LocalMessageSimpleInitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalMessageRepository localMessageRepository(LocalMessageDao repository) {
        return new JpaLocalMessageRepository(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalMessageManager localMessageManager(LocalMessageRepository localMessageRepository,
                                                   TransactionHelper transactionHelper,
                                                   MsgSender msgSender) {
        return new DefaultLocalMessageManager(localMessageRepository, transactionHelper, msgSender);
    }
}
