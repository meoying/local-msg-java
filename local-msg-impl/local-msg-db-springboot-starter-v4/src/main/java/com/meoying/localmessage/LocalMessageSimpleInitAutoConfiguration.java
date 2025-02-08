package com.meoying.localmessage;

import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.msg.TestMsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.JpaLocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.LocalMessageDao;
import com.meoying.localmessage.simple.DefaultLocalMessageManager;
import com.meoying.localmessage.utils.TransactionHelper;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import com.meoying.localmessage.v4.api.TransactionSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static com.meoying.localmessage.LocalMessageStarterAutoConfiguration.DEFAULT_CACHE_NAME;

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
