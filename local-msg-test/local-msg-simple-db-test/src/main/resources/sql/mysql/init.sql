-- 创建
create database msg_test default character set utf8mb4;
-- 使用
use msg_test;

SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_admin
-- ----------------------------
DROP TABLE IF EXISTS `local_message`;

SET
GLOBAL transaction_isolation = 'READ-COMMITTED';

CREATE TABLE `local_message`
(
    `id`            bigint AUTO_INCREMENT PRIMARY KEY,
    `topic`         varchar(50),
    `msg`           varchar(1000),
    `status`        int,
    `retry_count`   int,
    `data_chg_time` bigint
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- 解决死锁问题，防止更新时使用行锁锁住所有行再退化
ALTER TABLE local_message
    ADD INDEX idx_data_chg_time (data_chg_time);


