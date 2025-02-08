package com.meoying.localmessage;

import com.meoying.localmessage.configuration.LocalMessageProperties;
import com.meoying.localmessage.msg.MsgSender;
import com.meoying.localmessage.repository.LocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.JpaShardingLocalMessageRepository;
import com.meoying.localmessage.repository.impl.jpa.ShardingLocalMessageDaoCustom;
import com.meoying.localmessage.repository.impl.jpa.ShardingLocalMessageDaoImpl;
import com.meoying.localmessage.sharding.datasource.ShardingRoutingDataSource;
import com.meoying.localmessage.sharding.table.TableNameRouter;
import com.meoying.localmessage.simple.ShardingLocalMessageManager;
import com.meoying.localmessage.utils.TransactionHelper;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Configuration
@AutoConfigureAfter({LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage",
        name = "type", havingValue = "sharding"
)
public class LocalMessageShardingInitAutoConfiguration {

    @Bean(name = "shardingRoutingDataSource")
    @ConditionalOnMissingBean(name = "shardingRoutingDataSource")
    @Primary
    public DataSource shardingRoutingDataSource(ApplicationContext applicationContext,
                                                LocalMessageProperties localMessageProperties) {

        String defaultDataSourceName = localMessageProperties.getDefaultDataSourceName();
        Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
        DataSource dataSource = dataSourceMap.get(defaultDataSourceName);

        Map<Object, Object> map = dataSourceMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue));
        return new ShardingRoutingDataSource(dataSource, map,
                dataSourceMap.keySet());
    }

    @Bean
    @ConditionalOnMissingBean
    public TableNameRouter tableNameRouter(LocalMessageProperties localMessageProperties) {
        return new TableNameRouter(localMessageProperties.getTableNameMap());
    }

    @Bean
    @ConditionalOnMissingBean
    public ShardingLocalMessageDaoCustom shardingLocalMessageDaoCustom() {
        return new ShardingLocalMessageDaoImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalMessageRepository localMessageRepository(ShardingLocalMessageDaoCustom repository,LocalMessageProperties localMessageProperties) {
        return new JpaShardingLocalMessageRepository(repository,localMessageProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalMessageManager localMessageManager(LocalMessageRepository localMessageRepository,
                                                   TransactionHelper transactionHelper,
                                                   MsgSender msgSender,
                                                   TableNameRouter tableNameRouter) {
        return new ShardingLocalMessageManager(localMessageRepository, transactionHelper, msgSender,tableNameRouter);
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("MyThreadPool-");
        executor.initialize();
        return executor;
    }

}
