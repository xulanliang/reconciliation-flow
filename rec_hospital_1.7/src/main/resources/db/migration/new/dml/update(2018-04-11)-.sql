alter table t_device MODIFY COLUMN org_no varchar(50) NOT NULL;

ALTER TABLE `t_rec_pay_result`
MODIFY COLUMN `created_by_id`  int(20) NULL DEFAULT 1 COMMENT '创建者id' AFTER `Flow_No`,
MODIFY COLUMN `last_modified_by_id`  int(20) NULL DEFAULT 1 COMMENT '最后修改人id' AFTER `created_by_id`,
MODIFY COLUMN `last_modified_date`  datetime NULL COMMENT '最后修改时间' AFTER `last_modified_by_id`;