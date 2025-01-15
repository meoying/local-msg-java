package com.meoying.localmessage;

import com.meoying.localmessage.api.LocalMessageManagerV1;
import com.meoying.localmessage.api.TransactionV1;
import com.meoying.localmessage.configuration.localMessageProperties;
import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.JpaLocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.LocalMessageDao;
import com.meoying.localmessage.simple.DefaultLocalMessageManager;
import com.meoying.localmessage.utils.SpringTransactionSupport;
import com.meoying.localmessage.utils.TransactionHelper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(localMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.loaclmessage.base.enable",
        value = {"true"},
        matchIfMissing = true
)
public class LocalMessageInitAutoConfiguration {

    public TransactionV1 transactionV1(TransactionHelper transactionHelper) {
        return new SpringTransactionSupport(transactionHelper);
    }

    public LocalMessageRepository localMessageRepository(LocalMessageDao repository) {
        return new JpaLocalMessageRepository(repository);
    }

    public LocalMessageManagerV1 localMessageManagerV1(LocalMessageRepository localMessageRepository,
                                                       MsgSender msgSender) {
        return new DefaultLocalMessageManager(localMessageRepository, msgSender);
    }
}
