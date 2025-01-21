package com.meoying.localmessage.sharding.datasource;

import com.meoying.localmessage.core.utils.ShardingFuncThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ShardingRoutingDataSource extends AbstractRoutingDataSource {

    private final Logger logger=LoggerFactory.getLogger(ShardingRoutingDataSource.class);

    private final Set<String> dbName;

    public ShardingRoutingDataSource(DataSource defaultDatasource, Map<Object, Object> targetDataSources, Set<String> dbName) {
        super();
        this.dbName = dbName;

        if(Objects.isNull(defaultDatasource)){
            throw new IllegalArgumentException("defaultDatasource can not be null");
        }

        if(CollectionUtils.isEmpty(targetDataSources)){
            throw new IllegalArgumentException("targetDataSources can not be null");

        }

        super.setDefaultTargetDataSource(defaultDatasource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    /**
     * Determine the current lookup key. This will typically be
     * implemented to check a thread-bound transaction context.
     * <p>Allows for arbitrary keys. The returned key needs
     * to match the stored lookup key type, as resolved by the
     * {@link #resolveSpecifiedLookupKey} method.
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return ShardingFuncThreadLocal.getShardingFuncThreadLocal().get().getSharding().getLeft();
    }
}
