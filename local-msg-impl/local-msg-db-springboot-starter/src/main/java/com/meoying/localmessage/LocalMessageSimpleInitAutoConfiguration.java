package com.meoying.localmessage;

import com.meoying.localmessage.api.LocalMessageManager;
import com.meoying.localmessage.api.Transaction;
import com.meoying.localmessage.configuration.localMessageProperties;
import com.meoying.localmessage.core.cache.Cache;
import com.meoying.localmessage.core.cache.memory.GuavaCache;
import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.msg.TestMsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.JpaLocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.LocalMessageDao;
import com.meoying.localmessage.simple.DefaultLocalMessageManager;
import com.meoying.localmessage.utils.SpringTransactionSupport;
import com.meoying.localmessage.utils.TransactionHelper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(localMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage",
        name = "type", havingValue = "simple"
)
public class LocalMessageSimpleInitAutoConfiguration {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionHelper transactionHelper(PlatformTransactionManager transactionManager) {
        Cache<TransactionDefinition, TransactionTemplate> cache =
                new GuavaCache.GuavaCacheBuilder<TransactionDefinition, TransactionTemplate>().expireAfterWrite(1L,
                        TimeUnit.HOURS).build();
        return new TransactionHelper(cache, transactionManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public Transaction transaction(TransactionHelper transactionHelper) {
        return new SpringTransactionSupport(transactionHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalMessageRepository localMessageRepository(LocalMessageDao repository) {
        return new JpaLocalMessageRepository(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public MsgSender msgSender(){
        return new TestMsgSender();
    }


    @Bean
    @ConditionalOnMissingBean
    public LocalMessageManager localMessageManager(LocalMessageRepository localMessageRepository,
                                                     MsgSender msgSender) {
        return new DefaultLocalMessageManager(localMessageRepository, msgSender);
    }
}
