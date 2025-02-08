-- 创建
create database msg_test default character set utf8mb4;
-- 使用
use msg_test;

SET
FOREIGN_KEY_CHECKS = 0;

SET
GLOBAL transaction_isolation = 'READ-COMMITTED';

DROP TABLE IF EXISTS `local_message`;

CREATE TABLE `local_message`
(
    `id`            bigint AUTO_INCREMENT PRIMARY KEY,
    `topic`         varchar(50),
    `msg`           varchar(1000),
    `status`        int,
    `retry_count`   int,
    `data_chg_time` bigint
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

ALTER TABLE local_message
    ADD INDEX idx_data_chg_time (data_chg_time);

DROP TABLE IF EXISTS `tb_biz`;

CREATE TABLE `tb_biz`
(
    `id`            bigint AUTO_INCREMENT PRIMARY KEY,
    `biz`           varchar(1000)
) ENGINE = InnoDB  DEFAULT CHARSET = utf8;
