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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(localMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage.base.enable",
        value = {"true"},
        matchIfMissing = true
)
@EntityScan({"com.meoying.localmessage.repository.entity"})
@EnableJpaRepositories({"com.meoying.localmessage.repository.impl.jpa"})
public class LocalMessageJpaAutoConfiguration {


}
