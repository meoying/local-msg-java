package com.meoying.localmessage;

import com.meoying.localmessage.configuration.localMessageProperties;
import com.meoying.localmessage.sharding.datasource.ShardingRoutingDataSource;
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
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(localMessageProperties.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, LocalMessageStarterAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage.base.sharding.enable",
        value = {"true"},
        matchIfMissing = true
)
public class LocalMessageShardingInitAutoConfiguration {

    @Bean(name = "shardingRoutingDataSource")
    @ConditionalOnMissingBean
    public DataSource shardingRoutingDataSource(ApplicationContext applicationContext,
                                                localMessageProperties localMessageProperties) {
//        Map<String, DataSource> dataSourceMap = dataSources.stream()
//                .collect(Collectors.toMap(DataSource::toString, (dataSource -> dataSource)));
        String defaultDataSourceName = localMessageProperties.getDefaultDataSourceName();
        Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
        DataSource dataSource = dataSourceMap.get(defaultDataSourceName);
        Map<Object, Object> map = dataSourceMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue));
        ShardingRoutingDataSource shardingRoutingDataSource = new ShardingRoutingDataSource(dataSource, map);
        return shardingRoutingDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("shardingRoutingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }



}
