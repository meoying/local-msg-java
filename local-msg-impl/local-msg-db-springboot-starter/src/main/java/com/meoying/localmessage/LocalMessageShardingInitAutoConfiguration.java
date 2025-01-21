package com.meoying.localmessage;

import com.meoying.localmessage.configuration.localMessageProperties;
import com.meoying.localmessage.core.cache.Cache;
import com.meoying.localmessage.core.cache.memory.GuavaCache;
import com.meoying.localmessage.sharding.datasource.ShardingRoutingDataSource;
import com.meoying.localmessage.sharding.table.TableNameRouter;
import com.meoying.localmessage.utils.TransactionHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(localMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage.type",
        name = "type", havingValue = "sharding"
)
public class LocalMessageShardingInitAutoConfiguration {

    @Bean(name = "shardingRoutingDataSource")
    @ConditionalOnMissingBean
    public DataSource shardingRoutingDataSource(ApplicationContext applicationContext,
                                                localMessageProperties localMessageProperties) {

        String defaultDataSourceName = localMessageProperties.getDefaultDataSourceName();
        Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
        DataSource dataSource = dataSourceMap.get(defaultDataSourceName);

        Map<Object, Object> map = dataSourceMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue));
        return new ShardingRoutingDataSource(dataSource, map,
                dataSourceMap.keySet());
    }

    @Bean()
    @ConditionalOnMissingBean
    public TableNameRouter tableNameRouter(localMessageProperties localMessageProperties) {
        return new TableNameRouter(localMessageProperties.getTableNameMap());
    }


    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("shardingRoutingDataSource") DataSource dataSource) {
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



}
