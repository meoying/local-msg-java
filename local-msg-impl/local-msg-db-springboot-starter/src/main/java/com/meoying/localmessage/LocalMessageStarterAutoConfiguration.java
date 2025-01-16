package com.meoying.localmessage;

import com.meoying.localmessage.configuration.localMessageProperties;
import com.meoying.localmessage.core.logging.LogFactory;
import com.meoying.localmessage.core.logging.Logger;
import com.meoying.localmessage.logging.Slf4jImpl;
import com.meoying.localmessage.utils.TransactionHelper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(localMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage.base.enable",
        value = {"true"},
        matchIfMissing = true
)
public class LocalMessageStarterAutoConfiguration {

    private final DataSource dataSource;
    private final localMessageProperties localMessageProperties;

    public LocalMessageStarterAutoConfiguration(DataSource dataSource, localMessageProperties localMessageProperties) {
        this.dataSource = dataSource;
        this.localMessageProperties = localMessageProperties;
        if (StringUtils.hasText(localMessageProperties.getLoggerClass())) {
            LogFactory.useCustomLogging(Slf4jImpl.class);
        } else {
            try {
                Class<?> aClass = Class.forName(localMessageProperties.getLoggerClass());
                if (Logger.class.isAssignableFrom(aClass)) {
                    LogFactory.useCustomLogging((Class<? extends Logger>) aClass);
                } else {
                    LogFactory.useStdOutLogging();
                    System.out.println("cant found log:[" + localMessageProperties.getLoggerClass() + "]");
                }
            } catch (ClassNotFoundException e) {
                System.err.println("cant found log:[" + localMessageProperties.getLoggerClass() + "]");
                throw new RuntimeException(e);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionHelper transactionHelper(PlatformTransactionManager transactionManager) {
        return new TransactionHelper(transactionManager);
    }

}
