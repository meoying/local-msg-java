package com.meoying.localmessage;

import com.meoying.localmessage.repository.entity.TbBiz;
import com.meoying.localmessage.repository.jpa.TbBizRepository;
import com.meoying.localmessage.v4.api.LocalMessageManager;
import com.meoying.localmessage.v4.api.Message;
import com.meoying.localmessage.v4.api.StopFunc;
import com.meoying.localmessage.v4.api.sharding.MsgTable;
import com.meoying.localmessage.v4.api.sharding.ShardingFuncThreadLocal;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Test
    public void testShardingDb() {
        ShardingFuncThreadLocal.set(() -> new MsgTable() {
            @Override
            public String getDbName() {
                return "ds2";
            }

            @Override
            public String getTableName() {
                return "local_message2";
            }
        });
        try {
            Integer integers = jdbcTemplate.queryForObject("select count(id) from local_message2", Integer.class);
            logger.info("count:{}", integers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
            logger.info("count:{}", integers);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            ShardingFuncThreadLocal.remove();
        }
    }

    @Test
    public void testBiz() {

        localMessageManager.doWithShardingLocalMessage(() -> new MsgTable() {
            @Override
            public String getDbName() {
                return "ds2";
            }

            @Override
            public String getTableName() {
                return "local_message2";
            }
        },() -> {
            logger.info("biz start");
            int i = 9;
            return () -> new Message("msg", "topic");
        });


        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBiz1() {

        localMessageManager.doWithShardingLocalMessage(() -> new MsgTable() {
            @Override
            public String getDbName() {
                return "ds2";
            }

            @Override
            public String getTableName() {
                return "local_message";
            }
        },() -> {
            logger.info("biz start");
            throw new RuntimeException("业务问题");
        });


        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFixMessage() {
        StopFunc stopFunc = localMessageManager.fixMessage();

        try {
            Thread.sleep(1000000L);
            stopFunc.done();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBiz3() {

        localMessageManager.doWithLocalMessage(() -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("saasasda");
            tbBizRepository.save(entity);
            return () -> new Message("topic", "saasasda");
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBiz4() {

        localMessageManager.doWithShardingLocalMessage(() -> new MsgTable() {
            @Override
            public String getDbName() {
                return "ds2";
            }

            @Override
            public String getTableName() {
                return "local_message";
            }
        },() -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("2025020801");
            tbBizRepository.save(entity);
            return () -> new Message("topic", "2025020801");
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testBiz5() {

        localMessageManager.doWithShardingLocalMessage(() -> new MsgTable() {
            @Override
            public String getDbName() {
                return "ds2";
            }

            @Override
            public String getTableName() {
                return "local_message";
            }
        },() -> {
            TbBiz entity = new TbBiz();
            entity.setBiz("20250208");
            tbBizRepository.save(entity);
            int i=1/0;
            return () -> new Message("topic", "20250208");
        });

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
