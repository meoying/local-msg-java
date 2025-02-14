package com.meoying.localmessage;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.meoying.localmessage.configuration.LocalMessageProperties;
import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.msg.TestMsgSender;
import com.meoying.localmessage.utils.TransactionHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableConfigurationProperties(LocalMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage",
        name = "enable", havingValue = "true", matchIfMissing = true
)
public class LocalMessageStarterAutoConfiguration {

    public final static String DEFAULT_CACHE_NAME = "localMessageCache";

    @Bean("defaultCacheManager")
    @ConditionalOnMissingBean
    public CaffeineCacheManager defaultCacheManager() {
        CaffeineCacheManager defaultCacheManager = new CaffeineCacheManager(DEFAULT_CACHE_NAME);
        defaultCacheManager.setAllowNullValues(true);
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(64)
                .maximumSize(512)
                .expireAfterWrite(12, TimeUnit.HOURS); //过期时间
        defaultCacheManager.setCaffeine(caffeineBuilder);
        return defaultCacheManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public MsgSender msgSender() {
        return new TestMsgSender();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionHelper transactionHelper(@Qualifier("defaultCacheManager") CacheManager cacheManager,
                                               PlatformTransactionManager transactionManager) {
        Cache cache = cacheManager.getCache(DEFAULT_CACHE_NAME);
        return new TransactionHelper(cache, transactionManager);
    }
}
