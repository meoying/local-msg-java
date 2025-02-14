package com.meoying.localmessage;

import com.meoying.localmessage.repository.entity.TbBiz;
import com.meoying.localmessage.repository.jpa.TbBizRepository;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.StopFunc;
import com.meoying.localmessage.v4.api.sharding.MsgTable;
import com.meoying.localmessage.v4.api.sharding.ShardingFuncThreadLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = DemoTestV4.class)
@EnableAutoConfiguration
@ComponentScan("com.meoying.test")
@EntityScan({"com.meoying.localmessage.repository.entity"})
@EnableJpaRepositories({"com.meoying.localmessage.repository.jpa"})
public class DemoTestV4 {

    private final Logger logger = LoggerFactory.getLogger(DemoTestV4.class);

    @Autowired
    LocalMessageManager localMessageManager;

    @Autowired
    TbBizRepository tbBizRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("ds1")
    DataSource ds1;

    @Autowired
    @Qualifier("ds2")
    DataSource ds2;

    MsgTable ds_local_message2 = new MsgTable() {
        @Override
        public String getDbName() {
            return "ds2";
        }

        @Override
        public String getTableName() {
            return "local_message2";
        }
    };
    MsgTable ds_local_message = new MsgTable() {
        @Override
        public String getDbName() {
            return "ds";
        }

        @Override
        public String getTableName() {
            return "local_message";
        }
    };
    MsgTable ds2_local_message2 = new MsgTable() {
        @Override
        public String getDbName() {
            return "ds2";
        }

        @Override
        public String getTableName() {
            return "local_message2";
        }
    };
    MsgTable ds2_local_message = new MsgTable() {
        @Override
        public String getDbName() {
            return "ds2";
        }

        @Override
        public String getTableName() {
            return "local_message";
        }
    };


    @BeforeEach
    public void BeforeTest() {
        try (Connection connection = ds1.getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE local_message2");
            stmt.executeUpdate("TRUNCATE TABLE local_message");
            stmt.executeUpdate("TRUNCATE TABLE tb_biz");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = ds2.getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE local_message2");
            stmt.executeUpdate("TRUNCATE TABLE local_message");
            stmt.executeUpdate("TRUNCATE TABLE tb_biz");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    // 测试组合数据源是否生效
    public void testShardingDb() {
        try (Connection connection = ds1.getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO local_message (id, topic) VALUES (1, 'test message')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = ds2.getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO local_message2 (id, topic) VALUES (1, 'test message')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ShardingFuncThreadLocal.set(() -> ds2_local_message2);
        try {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message2", Integer.class);
            assertEquals(1, integers, "The count should be 1");
        } finally {
            ShardingFuncThreadLocal.remove();
        }
        ShardingFuncThreadLocal.set(() -> new MsgTable() {
            @Override
            public String getDbName() {
                return "ds1";
            }

            @Override
            public String getTableName() {
                return "local_message";
            }
        });
        try {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message", Integer.class);
            assertEquals(1, integers, "The count should be 1");
        } finally {
            ShardingFuncThreadLocal.remove();
        }
    }

    @Test
    // 测试分库分表情况下的本地消息存储
    public void testBiz() {
        localMessageManager.doWithShardingLocalMessage(() -> ds2_local_message2, () -> {
            logger.info("biz start");
            int i = 9;
            return () -> new Message("msg", "topic");
        });

        ShardingFuncThreadLocal.warp(() -> ds2_local_message2, () -> {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message2", Integer.class);
            assertEquals(1, integers, "The count should be 1");
            return null;
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    // 测试分库分表情况下本地消息的回滚
    public void testBiz1() {
        try {
            localMessageManager.doWithShardingLocalMessage(() -> ds2_local_message, () -> {
                logger.info("biz start");
                throw new RuntimeException("业务问题");
            });
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass(), "e should be RuntimeException");
            assertEquals("java.lang.RuntimeException: 业务问题", e.getMessage(), "Message should be 业务问题");
        }

        ShardingFuncThreadLocal.warp(() -> ds2_local_message, () -> {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message", Integer.class);
            assertEquals(0, integers, "The count should be 0");
            return null;
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    // 测试分库分表情况下的消息修复
    public void testFixMessage() {
        int retryCount = 3;

        try (Connection connection = ds1.getConnection(); Statement stmt = connection.createStatement()) {
            for (int i = 1; i < 11; i++) {
                stmt.executeUpdate("INSERT INTO local_message (id,msg, topic, status, retry_count, data_chg_time) " +
                        "VALUES (" + i + ",'1234', 'ds',0, " + i % retryCount + ", 1739516603044)");
                stmt.executeUpdate("INSERT INTO local_message2 (id,msg, topic, status, retry_count, data_chg_time) " +
                        "VALUES (" + i + ",'1234', 'ds', 0, " + i % retryCount + ", 1739516603044)");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = ds2.getConnection(); Statement stmt = connection.createStatement()) {
            for (int i = 1; i < 11; i++) {
                stmt.executeUpdate("INSERT INTO local_message (id,msg, topic, status, retry_count, data_chg_time) " +
                        "VALUES (" + i + ",'12345', 'ds2', 0, " + i % retryCount + ", 1739516603044)");
                stmt.executeUpdate("INSERT INTO local_message2 (id,msg, topic, status, retry_count, data_chg_time) " +
                        "VALUES (" + i + ",'12345', 'ds2', 0, " + i % retryCount + ", 1739516603044)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StopFunc stopFunc = localMessageManager.fixMessage();

        try {
            Thread.sleep(60000L);
            stopFunc.done();

            ShardingFuncThreadLocal.warp(() -> ds_local_message, () -> {
                Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message where status =2 " +
                        "or (retry_count=" + retryCount + 1 + " and status =3)", Integer.class);
                assertEquals(10, integers, "The count should be 10");
                return null;
            });

            ShardingFuncThreadLocal.warp(() -> ds_local_message2, () -> {
                Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message2 where status =2 " +
                        "or (retry_count=" + retryCount + 1 + " and status =3)", Integer.class);
                assertEquals(10, integers, "The count should be 10");
                return null;
            });

            ShardingFuncThreadLocal.warp(() -> ds2_local_message, () -> {
                Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message where status =2 " +
                        "or (retry_count=" + retryCount + 1 + " and status =3)", Integer.class);
                assertEquals(10, integers, "The count should be 10");
                return null;
            });

            ShardingFuncThreadLocal.warp(() -> ds2_local_message2, () -> {
                Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message2 where status =2 " +
                        "or (retry_count=" + retryCount + 1 + " and status =3)", Integer.class);
                assertEquals(10, integers, "The count should be 10");
                return null;
            });

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    // 测试默认数据源
    public void testBiz3() {

        localMessageManager.doWithLocalMessage(() -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("saasasda");
            tbBizRepository.save(entity);
            return () -> new Message("topic", "saasasda");
        });

        Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message where topic='topic'",
                Integer.class);
        assertEquals(1, integers, "The count should be 1");
        Integer bizRes = jdbcTemplate.queryForObject("select count(id) from tb_biz where biz='saasasda'",
                Integer.class);
        assertEquals(1, bizRes, "The count should be 1");
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    // 测试本地消息表分库分表事务
    public void testBiz4() {

        localMessageManager.doWithShardingLocalMessage(() -> ds2_local_message, () -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("2025020801");
            tbBizRepository.save(entity);
            return () -> new Message("topic", "2025020801");
        });

        ShardingFuncThreadLocal.warp(() -> ds2_local_message, () -> {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message where topic='topic' " +
                    "AND msg='2025020801'", Integer.class);
            assertEquals(1, integers, "The count should be 1");
            Integer bizRes = jdbcTemplate.queryForObject("select count(id) from tb_biz where biz='2025020801'",
                    Integer.class);
            assertEquals(1, bizRes, "The count should be 1");
            return null;
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    // 测试分库分表本地消息表业务中断
    public void testBiz5() {
        try {
            localMessageManager.doWithShardingLocalMessage(() -> ds2_local_message, () -> {
                TbBiz entity = new TbBiz();
                entity.setBiz("20250208");
                tbBizRepository.save(entity);
                int i = 1 / 0;
                return () -> new Message("topic", "20250208");
            });
        } catch (Exception ignore) {

        }

        ShardingFuncThreadLocal.warp(() -> ds2_local_message, () -> {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message where topic='topic' " +
                    "AND msg='20250208'", Integer.class);
            assertEquals(0, integers, "The count should be 0");
            Integer bizRes = jdbcTemplate.queryForObject("select count(id) from tb_biz where biz='20250208'",
                    Integer.class);
            assertEquals(0, bizRes, "The count should be 0");
            return null;
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
