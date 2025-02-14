package com.meoying.localmessage.repository.impl.jpa;

import com.meoying.localmessage.repository.entity.LocalMessage;
import com.meoying.localmessage.utils.SnowflakeIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ShardingLocalMessageDaoImpl implements ShardingLocalMessageDaoCustom {

    private static final Logger log = LoggerFactory.getLogger(ShardingLocalMessageDaoImpl.class);
    private final SnowflakeIdGenerator idGenerator;
    @PersistenceContext
    private EntityManager entityManager;

    public ShardingLocalMessageDaoImpl() {
        idGenerator = new SnowflakeIdGenerator(1, 1);
    }

    @Override
    public int updateStatusSuccess(String tableName, long id, int newStatus, int oldStatus) {
        String sql = "UPDATE " + tableName + " SET status = :newStatus, data_chg_time = :timestamp WHERE id = " +
                ":id AND status = :oldStatus";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("newStatus", newStatus);
        query.setParameter("id", id);
        query.setParameter("oldStatus", oldStatus);
        long timestamp = System.currentTimeMillis();
        query.setParameter("timestamp", timestamp);
        return query.executeUpdate();
    }

    @Override
    public int updateRetryCount(String tableName, long id, int status) {
        String sql = "UPDATE " + tableName + " SET retry_count = retry_count + 1, data_chg_time = :timestamp " +
                "WHERE id = :id AND status = :status";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("id", id);
        query.setParameter("status", status);
        long timestamp = System.currentTimeMillis();
        query.setParameter("timestamp", timestamp);
        return query.executeUpdate();
    }

    @Override
    public Page<LocalMessage> findMessageByPageSize(String tableName, int maxRetryCount, long timestamp,
                                                    Pageable pageable) {
        String sql = "SELECT * FROM " + tableName + " WHERE status=0 AND retry_count <= :maxRetryCount AND " +
                "data_chg_time < " +
                ":timestamp order by data_chg_time ";
        Query query = entityManager.createNativeQuery(sql, LocalMessage.class);
        query.setParameter("maxRetryCount", maxRetryCount);
        query.setParameter("timestamp", timestamp);
        //log.info(pageable.toString());
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<LocalMessage> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    @Override
    public int failLocalMessage(String tableName, int maxRetryCount) {
        String sql = "UPDATE " + tableName + " SET status = 3, data_chg_time = :timestamp  WHERE retry_count " +
                "> :maxRetryCount AND status = 0";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("maxRetryCount", maxRetryCount);
        long timestamp = System.currentTimeMillis();
        query.setParameter("timestamp", timestamp);
        return query.executeUpdate();
    }

    @Override
    public LocalMessage save(String tableName, LocalMessage message) {
        if (message.getId() == 0L) {
            String sql = "INSERT INTO " + tableName + " (id,msg, topic, status, retry_count, data_chg_time) VALUES " +
                    "(:id,:msg, :topic, :status, :retryCount, :dataChgTime)";
            Query query = entityManager.createNativeQuery(sql);
            long id = idGenerator.nextId();
            message.setId(id);
            query.setParameter("id", id);
            query.setParameter("msg", message.getMsg());
            query.setParameter("topic", message.getTopic());
            query.setParameter("status", message.getStatus());
            query.setParameter("retryCount", message.getRetryCount());
            query.setParameter("dataChgTime", message.getDataChgTime());
            query.executeUpdate();
        } else {
            String sql = "UPDATE " + tableName + " SET msg = :msg, topic = :topic, status = :status, retry_count = " +
                    ":retryCount, data_chg_time = :dataChgTime WHERE id = :id";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("msg", message.getMsg());
            query.setParameter("topic", message.getTopic());
            query.setParameter("status", message.getStatus());
            query.setParameter("retryCount", message.getRetryCount());
            query.setParameter("dataChgTime", message.getDataChgTime());
            query.setParameter("id", message.getId());
            query.executeUpdate();
        }
        return message;
    }
}