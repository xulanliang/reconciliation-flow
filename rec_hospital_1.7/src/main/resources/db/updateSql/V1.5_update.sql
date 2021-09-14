/** 支付结果上送表（更新） */
ALTER TABLE `t_order_upload`   
  DROP COLUMN `patient_areas`, 
  DROP COLUMN `device_no`, 
  DROP COLUMN `pay_source`, 
  DROP COLUMN `pay_type_detail`, 
  DROP COLUMN `cust_identify_type`, 
  DROP COLUMN `cust_name`, 
  DROP COLUMN `patient_id_card`, 
  DROP COLUMN `dept_no`, 
  DROP COLUMN `dept_name`, 
  DROP COLUMN `refund_no`, 
  DROP COLUMN `order_state_remark`, 
  DROP COLUMN `check_out`, 
  CHANGE `his_order_no` `his_order_no` VARCHAR(64) CHARSET utf8 COLLATE utf8_bin NULL   COMMENT '医院His系统订单号（不传视为His支付失败）'  AFTER `tsn_order_no`,
  CHANGE `order_no` `order_no` VARCHAR(64) CHARSET utf8 COLLATE utf8_bin NULL   COMMENT '综合支付平台订单号(接入综合支付平台的支付项目必填)'  AFTER `his_order_no`,
  ADD COLUMN `yb_serial_no` VARCHAR(64) NULL   COMMENT '医保流水号(结算方式非自费时节点必传)' AFTER `order_no`,
  ADD COLUMN `yb_bill_no` VARCHAR(64) NULL   COMMENT '医保结算单据号(结算方式非自费时节点必传)' AFTER `yb_serial_no`,
  ADD COLUMN `pay_total_amount` DECIMAL(10,2) NOT NULL   COMMENT '医疗总费用=记账金额+自费支付金额' AFTER `yb_bill_no`,
  CHANGE `pay_amount` `pay_amount` DECIMAL(10,2) NULL   COMMENT '自费支付金额（单位元，保留2位小数）',
  ADD COLUMN `yb_pay_amount` DECIMAL(10,2) NULL   COMMENT '记账合计金额（单位元，保留2位小数）' AFTER `pay_amount`,
  CHANGE `order_state` `order_state` VARCHAR(20) CHARSET utf8 COLLATE utf8_bin NULL   COMMENT '订单状态:已退款-1809304,支付完成-1809302,审核中-1809303,交易异常-1809300'  AFTER `yb_pay_amount`,
  ADD COLUMN `order_state_remark` VARCHAR(512) NULL   COMMENT '订单状态描述' AFTER `order_state`,
  CHANGE `trade_date_time` `trade_date_time` VARCHAR(20) NOT NULL   COMMENT '交易时间(yyyy-MM-dd HH:mm:ss)'  AFTER `order_state_remark`,
  CHANGE `trade_date` `trade_date` VARCHAR(10) CHARSET utf8 COLLATE utf8_bin NULL   COMMENT '交易日期(yyyy-MM-dd)'  AFTER `trade_date_time`,
  ADD COLUMN `settlement_type` VARCHAR(12) NOT NULL   COMMENT '结算方式：0031-自费、0131-医保、0231-公费、0331-农村合作医疗' AFTER `trade_date`,
  CHANGE `pay_type` `pay_type` VARCHAR(8) CHARSET utf8 COLLATE utf8_bin NOT NULL   COMMENT '支付类型：0049-现金、0149-银行卡、0249-微信、0349-支付宝、0449-医保、0549-网银、0649-聚合支付、0749-支票、9949-其他',
  CHANGE `pay_business_type` `pay_business_type` VARCHAR(8) CHARSET utf8 COLLATE utf8_bin NOT NULL   COMMENT '支付业务类型：0051-未知、0151-充值、0251-办卡、0351-补卡、0451-挂号、0551-缴费',
  CHANGE `pat_type` `pat_type` VARCHAR(8) CHARSET utf8 COLLATE utf8_bin NOT NULL   COMMENT '患者类型（mz:门诊，zy:住院，qt:其他）'  AFTER `pay_business_type`,
  CHANGE `bill_source` `bill_source` VARCHAR(16) CHARSET utf8 COLLATE utf8_bin NOT NULL   COMMENT '账单来源self:银医,self_jd:巨鼎,third:第三方'  AFTER `pat_type`,
  CHANGE `patient_card_no` `patient_card_no` VARCHAR(64) CHARSET utf8 COLLATE utf8_bin NOT NULL   COMMENT '患者就诊卡号'  AFTER `bill_source`,
  CHANGE `patient_name` `patient_name` VARCHAR(128) CHARSET utf8 COLLATE utf8_bin NOT NULL   COMMENT '患者姓名'  AFTER `patient_card_no`,
  CHANGE `cashier` `cashier` VARCHAR(64) CHARSET utf8 COLLATE utf8_bin NULL   COMMENT '收费员/设备编码'  AFTER `patient_name`,
  CHANGE `good_info` `good_info` TEXT CHARSET utf8 COLLATE utf8_bin NULL   COMMENT '商品详情'  AFTER `cashier`, 
  DROP INDEX `orgCodeOutTradeNoUniqueIndex`,
  DROP INDEX `tsnOrderNoIndex`,
  DROP INDEX `hisOrderNoIndex`,
  DROP INDEX `tradeDateAndOrgCodeIndex`,
  ADD  UNIQUE INDEX `outTradeNoIndex` (`out_trade_no`),
  ADD  UNIQUE INDEX `tsnOrderNoIndex` (`tsn_order_no`),
  ADD  INDEX `orgCodeIndex` (`org_code`),
  ADD  INDEX `hisOrderNoIndex` (`his_order_no`),
  ADD  INDEX `orderNoIndex` (`order_no`),
  ADD  INDEX `patientCardNoIndex` (`patient_card_no`),
  ADD  INDEX `cashierIndex` (`cashier`),
  ADD  INDEX `tradeDateTimeIndex` (`trade_date_time`),
  ADD  INDEX `tradeDateIndex` (`trade_date`);
  
/* 退费结果上送表（新建） */
DROP TABLE IF EXISTS `t_refund_upload`;
CREATE TABLE `t_refund_upload` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_code` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '机构编码',
  `refund_order_no` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '退款流水号',
  `ori_tsn_order_no` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '原第三方业务系统流水号',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_date_time` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '退款日期时间',
  `refund_date` varchar(10) COLLATE utf8_bin NOT NULL COMMENT '退款日期',
  `cashier` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '操作员',
  PRIMARY KEY (`id`),
  UNIQUE KEY `refundOrderNoIndex` (`refund_order_no`),
  KEY `oriTsnOrderNoIndex` (`ori_tsn_order_no`),
  KEY `refundDateTimeIndex` (`refund_date_time`),
  KEY `refundDateIndex` (`refund_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/* 混合退费记录表（新建） */
CREATE TABLE `t_mix_refund`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_code` VARCHAR(32) COMMENT '机构编码',
  `settlement_type` VARCHAR(8) COMMENT '结算方式:0031-自费,0131-医保,0231-公费,0331-农村合作医疗',
  `refund_order_no` VARCHAR(64) COMMENT '退费单号',
  `refund_amount` DECIMAL(10,2) COMMENT '退费金额',
  `refund_date_time` VARCHAR(20) COMMENT '退费时间(格式yyyy-MM-dd HH:mm:ss)',
  `yb_pay_amount` DECIMAL(10,2) COMMENT '记账合计金额（单位元，保留2位小数）',
  `yb_serial_no` VARCHAR(64) COMMENT '医保流水号',
  `yb_bill_no` VARCHAR(64) COMMENT '医保结算单据号',
  `pay_business_type` VARCHAR(8) COMMENT '业务类型:0051-未知,0151-充值,0251-办卡,0351-补卡,0451-挂号,0551-缴费',
  `pat_type` VARCHAR(8) COMMENT '患者类型（mz:门诊，zy:住院，qt:其他）',
  `cashier` VARCHAR(64) COMMENT '收费员/设备编码',
  `refund_strategy` VARCHAR(2) DEFAULT '02' COMMENT '退款策略：01先进先出  02先大后小(默认)',
  `refund_reason` VARCHAR(512) COMMENT '退费原因',
  PRIMARY KEY (`id`)
) ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_bin;
ALTER TABLE `t_mix_refund`   
   ADD  INDEX `orgCodeIndex` (`org_code`),
   ADD  UNIQUE INDEX `refundOrderNoUniqueIndex` (`refund_order_no`),
   ADD  INDEX `refundDateIndex` (`refund_date_time`);

/* 混合退费记录明细表（新增） */  
CREATE TABLE `t_mix_refund_details`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tsn_order_no` VARCHAR(64) COMMENT '第三方业务系统流水号（微信支付宝、银行等支付成功返回的订单号）',
  `refund_order_no` VARCHAR(64) COMMENT '退费单号',
  `his_order_no` VARCHAR(64) COMMENT '医院His系统订单号',
  `pay_amount` DECIMAL(10,2) COMMENT '支付订单金额（单位元，保留2位小数）',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额（单位元，保留2位小数）',
  `pay_date_time` VARCHAR(20) COMMENT '支付时间，格式yyyy-MM-dd HH:mm:ss',
  `pay_type` VARCHAR(8) COMMENT '支付类型：0049-现金 ，0149-银行卡，0249-微信 ，0349-支付宝 ，0449-医保，0549-网银，0649-聚合支付，0749-支票，9949-其他',
  `bill_source` VARCHAR(8) COMMENT '账单来源：0030-建行自助（巨鼎-柯丽尔），0130-PAJK（平安好医生），0230-掌上医院（医享网-微信公众号），0330-医保支付（云医支付宝-支付宝生活号），0430-自助挂号（中行自助机），0530-宁远科技（就医160），0630-健康深圳，0830-HIS窗口',
  `refund_state` INT(8) COMMENT '退款状态：0-未退费，1-退费成功，2-正常退费失败， 3-异常退费失败',
  `refund_state_info` VARCHAR(2048) COMMENT '退款状态描述（失败原因）',
  PRIMARY KEY (`id`)
) ENGINE=MYISAM CHARSET=utf8 COLLATE=utf8_bin;
ALTER TABLE `t_mix_refund_details`   
  ADD  INDEX `refundOrderNoIndex` (`refund_order_no`);

/* 2018-11-01 异常处理表（修改）*/
ALTER TABLE `t_trade_check_follow_deal`   
  ADD COLUMN `exception_state` VARCHAR(8) NULL   COMMENT '异常状态：长款-26、短款-35' AFTER `created_date`,
  ADD COLUMN `deal_amount` DECIMAL(10,2) NULL   COMMENT '处理金额' AFTER `exception_state`;

/* 2018-11-02 字典表添加排序字段（修改）*/
ALTER TABLE `t_meta_data`   
  ADD COLUMN `sort` BIGINT(20) NULL   COMMENT '排序' AFTER `type_id`;

/* 2018-11-02 退费明细添加轮询次数和时间字段（修改）*/
ALTER TABLE `t_mix_refund_details`   
  ADD COLUMN `retry_times` INT(4) DEFAULT 0  NULL   COMMENT '重试次数' AFTER `refund_state_info`,
  ADD COLUMN `next_time` DATETIME NULL   COMMENT '下次重试时间' AFTER `retry_times`;
/* 医保中心医保his表添加机构时间联合索引*/
ALTER TABLE `t_healthcare_his`
ADD INDEX `org_time_his` (`org_no`, `trade_datatime`) USING BTREE ,
ADD INDEX `payNo_orderState_his` (`pay_flow_no`, `Order_State`) USING BTREE ;

ALTER TABLE `t_healthcare_official`
ADD INDEX `org_time` (`org_no`, `trade_datatime`) USING BTREE ,
ADD INDEX `payNo_orderState` (`pay_flow_no`, `Order_State`) USING BTREE ;
/* 医保异常表索引添加*/
ALTER TABLE `t_health_exception`
ADD INDEX `org_time_exception` (`org_no`, `trade_data_time`) USING BTREE ,
ADD INDEX `payNo_orderState_exception` (`pay_flow_no`, `order_state`) USING BTREE ;
/* 2018-11-13 退费明细添加退费次数字段（修改）*/
ALTER TABLE `t_mix_refund_details`
ADD COLUMN `refund_count`  int(2) NULL DEFAULT 0 COMMENT '退费次数' AFTER `next_time`;

/* 2018-11-16 新增字典和系统编码的对应关系维护表*/
CREATE TABLE `t_system_metadata`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_code` VARCHAR(64) NOT NULL COMMENT '医院编码',
  `meta_data_code` VARCHAR(64) NOT NULL COMMENT '字典值',
  `system_code` VARCHAR(64) NOT NULL COMMENT '系统编码',
  `sort_key` INT(10) DEFAULT 0 COMMENT '排序字段',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `meta_data_code_org_code_unique_index` (`org_code`, `meta_data_code`, `system_code`)
) ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_croatian_ci;

/**
 * 去掉医院编码
 */
ALTER TABLE `t_system_metadata`
DROP COLUMN `org_code`,
DROP INDEX `meta_data_code_org_code_unique_index` ,
ADD UNIQUE INDEX `meta_data_code_unique_index` (`meta_data_code`, `system_code`) USING BTREE;

/** 异常处理表，添加交易时间，机构编码 */
ALTER TABLE `t_trade_check_follow_deal`   
  ADD COLUMN `org_code` VARCHAR(64) NULL   COMMENT '机构编码' AFTER `deal_amount`,
  ADD COLUMN `trade_datetime` VARCHAR(20) NULL   COMMENT '交易时间' AFTER `org_code`;

/** 支付结果上送表：添加支付位置字段 */
ALTER TABLE `t_order_upload`   
  ADD COLUMN `pay_location` VARCHAR(20) DEFAULT '0001'  NULL   COMMENT '支付位置：0001 自助机，0002窗口' AFTER `good_info`;

/** 2018-11-22 ：渠道表新增流水号字段索引 */
ALTER TABLE `t_thrid_bill`   
  ADD  INDEX `payFlowNoIndex` (`Pay_Flow_No`);

/** 2018-11-22 ：His表新增流水号字段索引 */
ALTER TABLE `t_rec_histransactionflow`   
  ADD  INDEX `payFlowNoIndex` (`Pay_Flow_No`);

/** 2018-11-23 ：异常记录表添加索引 */
ALTER TABLE `t_trade_check_follow`
ADD INDEX `orgNo_time` (`org_no`, `trade_time`, `check_state`) USING BTREE ,
ADD INDEX `businessNo` (`business_no`) USING BTREE ;

/** 2018-12-05 ：支付结果上送删除索引 */
ALTER TABLE `t_order_upload`   
  DROP INDEX `outTradeNoIndex`,
  DROP INDEX `hisOrderNoIndex`,
  DROP INDEX `orderNoIndex`,
  DROP INDEX `patientCardNoIndex`,
  DROP INDEX `cashierIndex`,
  DROP INDEX `tradeDateTimeIndex`,
  DROP INDEX `tradeDateIndex`;
  
/** 2018-12-06 ：his报表汇总中间表 */
DROP TABLE IF EXISTS `t_his_report`;
CREATE TABLE `t_his_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_code` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '机构编码',
  `trade_date` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '账单日期',
  `bill_source` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '账单来源',
  `pay_business_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '业务类型',
  `pat_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '患者类型',
  `pay_location` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '支付位置',
  `order_state` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '订单状态',
  `pay_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '支付方式',
  `cashier` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '收费员',
  `pay_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `pay_acount` int(10) NOT NULL DEFAULT '0' COMMENT '笔数',
  PRIMARY KEY (`id`),
  FULLTEXT KEY `orgCodeIndex` (`org_code`),
  FULLTEXT KEY `tradeDateIndex` (`trade_date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='报表统计汇总表';

/** 2018-12-06 ：支付结果上送添加字段退费状态，添加索引 */
ALTER TABLE `t_order_upload`   
  ADD COLUMN `refund_order_state` VARCHAR(20) NULL COMMENT '退费状态' AFTER `pay_location`;
ALTER TABLE `t_order_upload`   
  ADD  INDEX `orderStateIndex` (`order_state`),
  ADD  INDEX `refundOrderState` (`refund_order_state`);
  
/** 2018-12-07 ：异常订单退费记录表，添加索引 */
ALTER TABLE `t_exception_handling_record`   
  ADD  INDEX `stateIndex` (`state`),
  ADD  INDEX `fatherIdIndex` (`father_id`),
  ADD  FULLTEXT INDEX `orgCodeIndex` (`org_no`);
  
/** 2018-12-07 ：His账单表，添加索引 */
ALTER TABLE `t_rec_histransactionflow`
  ADD  KEY `orgCode_tradeDatatime_settlementDate` (`org_no`, `Trade_datatime`, `settlement_date`);

/** 2018-12-07 ：1.支付结果上送表添加索引 */
ALTER TABLE `t_order_upload`
ADD INDEX `orgCodeTradeDateTimeIndex` (`org_code`, `trade_date_time`) USING BTREE;  

/** 系统编码和字段配置信息表修改字段 */
ALTER TABLE `t_system_metadata`   
  DROP COLUMN `org_code`, 
  DROP INDEX `meta_data_code_org_code_unique_index`,
  ADD  UNIQUE INDEX `meta_data_code_org_code_unique_index` (`meta_data_code`, `system_code`);

/* 2019-01-10 唐都结算日对账需求新增：HIS结算日账单明细 */
CREATE TABLE `t_rec_his_settlement`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `his_order_no` VARCHAR(64) COMMENT 'HIS 订单号',
  `patient_id` VARCHAR(64) COMMENT '患者ID',
  `amount` DECIMAL(10,2) COMMENT '金额（2位小数）退费为负数  缴费为正数',
  `pay_type` VARCHAR(16) COMMENT '支付类型:微信（0249）或者支付宝（0349）',
  `order_type` VARCHAR(16) COMMENT '交易类型  缴费（0156）或者退费（0256）',
  `pay_time` DATETIME COMMENT '交易时间（yyyy-MM-dd HH:mm:ss）',
  `settlement_time` DATETIME COMMENT '结账时间（yyyy-MM-dd HH:mm:ss）',
  `settlement_date` DATE COMMENT '结账日期（yyyy-MM-dd）',
  `settlementor_num` VARCHAR(64) COMMENT '结算人编号',
  `tns_order_no` VARCHAR(64) COMMENT '第三方业务系统流水号（微信支付宝、银行等支付成功返回的订单号）',
  `bill_source` VARCHAR(64) COMMENT '账单来源:金蝶（self_td_jd）、巨鼎（self）',
  `settlement_number` VARCHAR(64) COMMENT '结算批次号',
  PRIMARY KEY (`id`)
);

/* 2019-01-10 唐都结算日对账需求新增：HIS结算日对账日志 */
CREATE TABLE `t_rec_log_settlement`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_date` VARCHAR(10) COMMENT '对账日期',
  `org_code` VARCHAR(20) COMMENT '机构编码',
  `created_date` DATETIME DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `rec_result` TINYINT(4) COMMENT '对账结果：70-异常，71-正常',
  `result_info` TEXT COMMENT '对账详情',
  PRIMARY KEY (`id`)
);

/* 2019-01-10 唐都结算日对账需求新增： HIS结算汇总表 */
CREATE TABLE `t_rec_his_settlement_result`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `settle_date` DATE NOT NULL DEFAULT '0000-00-00' COMMENT '账单日期',
  `channel_amount` DECIMAL(10,2) COMMENT '渠道汇总金额',
  `his_amount` DECIMAL(10,2) COMMENT 'HIS汇总金额',
  `his_settlement_amount` DECIMAL(10,2) COMMENT 'HIS结算金额',
  `yesterday_amount` DECIMAL(10,2) COMMENT '前一日金额',
  `today_unsettle_amount` DECIMAL(10,2) COMMENT '当日未结金额',
  PRIMARY KEY (`id`)
);

/* 2019-01-12 唐都结算日对账汇总新增账单来源字段： HIS结算汇总表 */
ALTER TABLE `t_rec_his_settlement_result`   
  ADD COLUMN `bill_source` VARCHAR(16) NULL   COMMENT '账单来源' AFTER `today_unsettle_amount`;
/* 2019-01-12 HIS结算明细表添加机构编码 */
ALTER TABLE `t_rec_his_settlement`   
  ADD COLUMN `org_code` VARCHAR(64) NULL   COMMENT '机构编码' AFTER `settlement_number`;
/* 2019-01-12 HIS结算明汇总添加机构编码 */
ALTER TABLE `t_rec_his_settlement_result`   
  ADD COLUMN `org_code` VARCHAR(64) NULL   COMMENT '机构编码' AFTER `bill_source`;

/* 2019-01-21 HIS结算明汇总添加结算以前金额 */  
ALTER TABLE `t_rec_his_settlement_result`
ADD COLUMN `before_settlement_amount`  decimal(10,2) NULL COMMENT '结算以前金额' AFTER `org_code`;

ALTER TABLE `t_rec_his_settlement_result`
MODIFY COLUMN `before_settlement_amount`  decimal(10,2) NULL DEFAULT 0 COMMENT '结算以前金额' AFTER `org_code`;

/* 2019-01-21 HIS账单表添加扩展字段：存json字符数据 */  
ALTER TABLE `t_rec_histransactionflow`   
  ADD COLUMN `extend_area` VARCHAR(1024) NULL   COMMENT '扩展字段（JSON字符）' AFTER `pay_location`;
/* 2019-01-21 渠道账单表添加扩展字段：存json字符数据 */   
ALTER TABLE `t_thrid_bill`   
  ADD COLUMN `extend_area` VARCHAR(1024) NULL   COMMENT '扩展字段' AFTER `bankfile_time`;

/* 2019-01-22 支付结果上送，添加扩展 */     
ALTER TABLE `t_order_upload`   
  ADD COLUMN `extend_area` VARCHAR(1024) NULL   COMMENT '扩展字段（JSON字符）' AFTER `refund_order_state`;
  
/* 2019-01-25 退费记录表，添加扩展 */ 
ALTER TABLE `t_exception_handling_record`
ADD COLUMN `extend_area`  varchar(1024) NULL DEFAULT NULL COMMENT '扩展字段' AFTER `bill_source`;

/* 2019-02-15 结算日对账新增字段 */ 
ALTER TABLE `t_rec_his_settlement_result`
ADD COLUMN `omission_amount`  decimal(10,2) NULL DEFAULT 0 COMMENT '遗漏未结金额' AFTER `before_settlement_amount`;

/* 2019-02-21 新增业务模块，窗口现金核对表 */ 
CREATE TABLE `t_window_cash`(  
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_code` VARCHAR(64) NOT NULL COMMENT '机构编码',
  `cash_date` DATETIME NOT NULL COMMENT '存款时间',
  `cashier_account` VARCHAR(64) COMMENT '收费员账号',
  `cashier_name` VARCHAR(64) COMMENT '收费员名称',
  `business_type` VARCHAR(20) COMMENT '业务类型（字典）',
  `bank_type` VARCHAR(20) COMMENT '银行类型（字典）',
  `cash_status` VARCHAR(20) COMMENT '状态：1 异常、0 正常、2 已通过',
  `his_amount` DECIMAL(10,2) COMMENT 'HIS 汇总金额（应收金额）',
  `channel_amount` DECIMAL(10,2) COMMENT '渠道到账金额（实收金额）',
  `exceptional_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '垫付金额',
  `exceptional_reason` VARCHAR(2048) COMMENT '垫付原因',
  `created_date_time` DATETIME COMMENT '创建时间',
  `check_date_time` DATETIME COMMENT '通过操作时间',
  `check_cashier_acount` VARCHAR(64) COMMENT '通过操作账号',
  `check_cashier_name` VARCHAR(64) COMMENT '通过操作人',
  PRIMARY KEY (`id`),
  INDEX `cashier_index` (`cashier_name`),
  INDEX `cash_date` (`cash_date`)
) ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_bin;

/* 2019-02-26 对账异常表，新增渠道和HIS账单ID */ 
ALTER TABLE `t_trade_check_follow`   
  ADD COLUMN `rec_his_id` BIGINT(20) NULL   COMMENT 'HIS账单ID' AFTER `terminal_no`,
  ADD COLUMN `rec_thrid_id` BIGINT(20) NULL   COMMENT '渠道账单ID' AFTER `rec_his_id`;

/* 2019-03-06 医保HIS和医保中心新增字段患者类型 */ 
ALTER TABLE `t_healthcare_official`
ADD COLUMN `patient_name`  varchar(20) NULL DEFAULT NULL COMMENT '患者名称' AFTER `pat_type`;
ALTER TABLE `t_healthcare_his`
ADD COLUMN `patient_name`  varchar(20) NULL DEFAULT NULL COMMENT '患者名称' AFTER `pat_type`;

/* 2019-03-08 His表新增商户号 */
ALTER TABLE `t_rec_histransactionflow`
  ADD COLUMN `pay_Shop_No` VARCHAR(50) NULL COMMENT '支付商户号' AFTER `Pay_Flow_No`;
/* 2019-03-08 His表新增商户订单号 */
ALTER TABLE `t_rec_histransactionflow`
  ADD COLUMN `shop_flow_no` VARCHAR(50) NULL COMMENT '商户订单号' AFTER `pay_Shop_No`;
/* 2019-03-08 渠道表新增参考号 */
ALTER TABLE `t_thrid_bill`
  ADD COLUMN `reference_num` VARCHAR(50) NULL COMMENT '参考号' AFTER `extend_area`;
/* 2019-03-08 His表新增参考号 */
ALTER TABLE `t_rec_histransactionflow`
  ADD COLUMN `reference_num` VARCHAR(50) NULL COMMENT '参考号' AFTER `extend_area`;
/* 2019-03-08 异常表新增参考号 */
ALTER TABLE `t_trade_check_follow`
  ADD COLUMN `reference_num` VARCHAR(50) NULL COMMENT '参考号' AFTER `rec_thrid_id`;
/* 2019-03-08 异常表新增商户流水号 */
ALTER TABLE `t_trade_check_follow`
  ADD COLUMN `shop_flow_no` VARCHAR(64) NULL COMMENT '商户流水号' AFTER `business_no`;

/* 2019-03-09 结算明细表新增患者名称和结算序号字段和商户流水号 */
ALTER TABLE  `t_rec_his_settlement`  
  ADD COLUMN `patient_name` VARCHAR(64) NULL COMMENT '患者姓名' AFTER `settlement_number`,
  ADD COLUMN `settlement_serial_no` VARCHAR(64) NULL COMMENT '结账序号' AFTER `patient_name`,
  ADD COLUMN `out_trade_no` VARCHAR(64) NULL COMMENT '商户流水号' AFTER `settlement_serial_no`;
/* 2019-3-10 医保渠道表新增商户流水号*/
ALTER TABLE t_healthcare_official ADD COLUMN shop_flow_no varchar(50) NULL COMMENT '商户流水号' AFTER `pat_type`;
/* 2019-3-10 异常账单表新增参考号字段*/
ALTER TABLE  `t_trade_check_follow`   
  ADD COLUMN `reference_num` varchar(50) NULL COMMENT '参考号' AFTER `rec_thrid_id`;
/* 2019-03-12 新增医保合计金额*/
ALTER TABLE `t_healthcare_his`
ADD COLUMN `cost_total_insurance`  decimal(12,2) NULL DEFAULT 0 COMMENT '医保合计金额=统筹基金金额+个账金额' AFTER `cost_subsidy`;
/* 2019-03-12 新增医保合计金额*/
ALTER TABLE `t_healthcare_official`
ADD COLUMN `cost_total_insurance`  decimal(12,2) NULL DEFAULT 0 COMMENT '医保合计金额=统筹基金金额+个账金额' AFTER `cost_subsidy`;
/* 2019-03-12 新增医保异常合计金额*/
ALTER TABLE `t_health_exception`
ADD COLUMN `cost_total_insurance`  decimal(12,2) NULL DEFAULT 0 COMMENT '医保合计金额=统筹基金金额+个账金额' AFTER `cost_account`;
/* 2019-03-12 新增医保异常总金额*/
ALTER TABLE `t_health_exception`
ADD COLUMN `cost_all`  decimal(12,2) NULL DEFAULT 0 COMMENT '医保合计金额=统筹基金金额+个账金额' AFTER `cost_total_insurance`;
/* 2019-03-18 新增支付业务类型字段 */
ALTER TABLE t_rec_his_settlement ADD COLUMN `pay_business_type` varchar(20) DEFAULT NULL COMMENT '支付业务类型';
/* 2019-03-28 医保异常表新增 医保his医保合计金额 */
ALTER TABLE `t_health_exception`
  MODIFY COLUMN `cost_total_insurance`  decimal(12,2) NULL DEFAULT 0.00 COMMENT '医保中心  医保合计金额=统筹基金金额+个账金额' AFTER `cost_account`,
  ADD COLUMN `cost_total_insurance_his`  decimal(12,2) NULL DEFAULT 0.00 COMMENT '医保his    医保合计金额=统筹基金金额+个账金额' AFTER `cost_total_insurance`;

/* 2019-03-29 对账异常是否退费，退费单号是否原生流水号 */
ALTER TABLE `t_rec_histransactionflow`  
  ADD COLUMN `require_refund` TINYINT(4) NULL   COMMENT '是否需要退费（0：否，1是）' AFTER `reference_num`,
  ADD COLUMN `pay_flow_no_type` TINYINT(4) NULL   COMMENT '是否渠道原生单号（微信42开头、支付宝年开头、银行卡单号）：（0：否，1是）' AFTER `require_refund`;
ALTER TABLE `t_thrid_bill`   
  ADD COLUMN `require_refund` TINYINT(4) NULL   COMMENT '是否需要退费（0：否，1是）' AFTER `reference_num`,
  ADD COLUMN `pay_flow_no_type` TINYINT(4) NULL   COMMENT '是否渠道原生单号（微信42开头、支付宝年开头、银行卡单号）：（0：否，1是）' AFTER `require_refund`;
ALTER TABLE `t_trade_check_follow`   
  ADD COLUMN `require_refund` TINYINT(4) NULL   COMMENT '是否需要退费（0：否，1是）' AFTER `reference_num`;
ALTER TABLE `t_order_upload`   
  ADD COLUMN `require_refund` TINYINT(4) NULL   COMMENT '是否需要退费（0：否，1是）',
  ADD COLUMN `pay_flow_no_type` TINYINT(4) NULL   COMMENT '是否渠道原生单号（微信42开头、支付宝年开头、银行卡单号）：（0：否，1是）' AFTER `require_refund`;

/* 2019-04-08 新增银行卡授权码 */
ALTER TABLE `t_thrid_bill`
  ADD COLUMN `authori_code` VARCHAR(20) NULL COMMENT '银行卡-授权码' AFTER `Trade_datatime`;

/* 2019-04-19 医保中心渠道表，新增社保卡号 */
ALTER TABLE `t_healthcare_official`
  ADD COLUMN `social_insurance_no` VARCHAR(50) NULL COMMENT '社保卡号' AFTER `operation_type`;

 /** 2019-4-25 修改医保中心表和医保his表的业务周期号长度	 */
ALTER TABLE `t_healthcare_official`   
  CHANGE `business_cycle_no` `business_cycle_no` VARCHAR(64) CHARSET utf8 COLLATE utf8_general_ci NULL COMMENT '业务周期号';
ALTER TABLE `t_healthcare_his`   
  CHANGE `business_cycle_no` `business_cycle_no` VARCHAR(64) CHARSET utf8 COLLATE utf8_general_ci NULL COMMENT '业务周期号';

  /* 2019-05-06 医保His表，新增医保电脑号 */
ALTER TABLE `t_healthcare_his`
ADD COLUMN `social_computer_number`  varchar(50) NULL COMMENT '社保电脑号' AFTER `operation_type`;
  /* 2019-05-06 医保异常表，新增医保电脑号 */
ALTER TABLE `t_health_exception`
ADD COLUMN `social_computer_number`  varchar(20) NULL COMMENT '社保电脑号' AFTER `health_code`;
  /* 2019-05-06 电子对账异常表，新增门诊号 */
ALTER TABLE `t_trade_check_follow`
ADD COLUMN `mz_code`  varchar(20) NULL COMMENT '门诊号' AFTER `pay_no`;


/* 2019-05-15 医保His表，新增业务类型（挂号/缴费） */
ALTER TABLE `t_healthcare_his`
  ADD COLUMN `busness_type` VARCHAR(20) NULL COMMENT '业务类型（挂号/缴费）' AFTER `pat_type`;
/* 2019-05-15 医保异常表，新增业务类型（挂号/缴费） */
ALTER TABLE `t_health_exception`
  ADD COLUMN `busness_type` VARCHAR(20) NULL COMMENT '业务类型（挂号/缴费）' AFTER `pat_type`;
/* 2019 5-17 新增邮件ftp等解析配置表*/
CREATE TABLE `t_bill_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_type` int(2) NOT NULL COMMENT '账单类型：1、邮件，2、ftp，3、本地',
  `bill_delimiter` varchar(5) DEFAULT NULL COMMENT '账单分割符',
  `bill_mapper` text NOT NULL COMMENT '账单映射',
  `bill_pay_type` int(2) DEFAULT NULL COMMENT '账单支付类型：1、微信，2、支付宝，3、银行',
  `pass_word` varchar(20) DEFAULT NULL COMMENT '密码',
  `url` varchar(50) DEFAULT NULL COMMENT '地址',
  `user_name` varchar(20) DEFAULT NULL COMMENT '用户名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `config_host` varchar(25) DEFAULT NULL COMMENT '邮件的服务器的host',
  `config_prot` varchar(10) DEFAULT NULL COMMENT '端口号',
  `config_agreement` varchar(10) DEFAULT NULL COMMENT '协议：pop3，smtp，imap',
  `config_add` varchar(100) NOT NULL,
  `config_row_start` int(2) DEFAULT NULL COMMENT '第几行开始解析',
  `config_line_start` int(2) DEFAULT NULL COMMENT '第几列开始解析',
  `config_flter` varchar(10) DEFAULT NULL COMMENT '要过滤的字段',
  `config_pay` varchar(10) DEFAULT NULL COMMENT '确定是付款的关键字段',
  `config_split` varchar(10) DEFAULT NULL COMMENT '解析行的分割字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/** 2019 5-27 添加操作表类型字段*/
ALTER TABLE `t_trade_check_follow_deal`
ADD COLUMN `pay_type`  varchar(10) NULL AFTER `trade_datetime`;

/** 2019 5-28 电子对账异常表 添加发票号*/
ALTER TABLE `t_trade_check_follow`
  ADD COLUMN `invoice_no` VARCHAR(50) NULL COMMENT '发票号' AFTER `business_no`;
/** 2019 5-28 电子对账渠道表 添加发票号*/
ALTER TABLE `t_thrid_bill`
  ADD COLUMN `invoice_no` VARCHAR(50) NULL COMMENT '发票号' AFTER `Ori_Pay_Flow_No`;
/** 2019 5-28 支付结果上送表 添加发票号*/
ALTER TABLE `t_order_upload`
  ADD COLUMN `invoice_no` VARCHAR(50) NULL COMMENT '发票号' AFTER `out_trade_no`;
/** 2019 5-29 添加操作表来源字段*/  
  ALTER TABLE `t_trade_check_follow_deal`
ADD COLUMN `bill_source`  varchar(10) NULL COMMENT '支付来源' AFTER `pay_type`;
/** 2019 5-31 添加退款表退费类型*/
ALTER TABLE `t_exception_handling_record`
ADD COLUMN `refund_type`  int NULL COMMENT '退款类型，1原路退回，2现金退回' AFTER `extend_area`;

/**	2019-6-6 新增配置信息表	**/
CREATE TABLE `t_properties_config` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `pkey` varchar(64) DEFAULT NULL,
  `pvalue` varchar(256) DEFAULT NULL,
  `default_value` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `sort` int(4) DEFAULT NULL,
  `is_actived` int(4) DEFAULT NULL,
  `model` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),cashier
  UNIQUE KEY `uniqueKeyIndex` (`pkey`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4

/**	2019-6-18 渠道账单表新增字段	**/
ALTER TABLE `t_thrid_bill`
  ADD COLUMN `cust_name` VARCHAR(64) NULL COMMENT '患者名称' ,
  ADD COLUMN `patient_card_no` VARCHAR(128) NULL COMMENT '患者卡号' AFTER `cust_name`,
  ADD COLUMN `card_type` VARCHAR(64) NULL COMMENT '卡类型' AFTER `patient_card_no`;
/**	2019-8-02 医保His表添加收费员字段	**/
ALTER TABLE `t_healthcare_his`
  ADD COLUMN `cashier` VARCHAR(50) NULL COMMENT '收费员' AFTER `social_computer_number`;
/**
 * 2019-8-02 his账单表新增字段
 */  
ALTER TABLE `t_rec_histransactionflow`
ADD COLUMN `cash_bill_source`  varchar(50) NULL COMMENT '现金数据来源' AFTER `pay_flow_no_type`;     

/**	2019-09-04 异常处理表新增患者类型字段	**/
ALTER TABLE `t_trade_check_follow_deal`
  ADD COLUMN `pat_type` VARCHAR(10) NULL COMMENT '患者类型' AFTER `bill_source`;

/**	2019-09-25 医保异常表新增 商户流水号 字段	**/
ALTER TABLE `t_health_exception`
  ADD COLUMN `shop_flow_no` VARCHAR(50) NULL COMMENT '商户流水号' AFTER `created_date`;
/** 2019-10-18 支付结果上送表添加：就诊记录号、终端号、可退款金额*/
  ALTER TABLE `t_order_upload`
ADD COLUMN `record_number`  varchar(64) NULL COMMENT '就诊记录号' AFTER `pay_flow_no_type`,
ADD COLUMN `terminal_number`  varchar(64) NULL COMMENT '终端号' AFTER `record_number`,
ADD COLUMN `returnable_amount`  decimal(12,2) NULL COMMENT '可退款金额' AFTER `terminal_number`;

  /**	2019-10-19  his汇总表新增终端号字段	**/
ALTER TABLE `t_his_report`   
  ADD COLUMN `terminal_no` VARCHAR(50) NULL COMMENT '终端号' AFTER `cashier`;

  /**	2019-10-21  渠道表新增用户线上聚合支付退款的三个字段	**/
ALTER TABLE `t_thrid_bill`   
  ADD COLUMN `union_pay_type` VARCHAR(16) NULL COMMENT '线上支付- 支付类型' AFTER `card_type`,
  ADD COLUMN `union_pay_code` VARCHAR(16) NULL COMMENT '线上支付-聚合支付标识' AFTER `union_pay_type`,
  ADD COLUMN `union_system_code` VARCHAR(16) NULL COMMENT '线上支付-系统编码' AFTER `union_pay_code`;
  /** 2019-10-23异常结果查询表添加4个索引*/
ALTER TABLE `t_trade_check_follow`
ADD INDEX `orgNo` (`org_no`) USING BTREE ,
ADD INDEX `billSource` (`bill_source`) USING BTREE ,
ADD INDEX `date` (`trade_date`) USING BTREE ,
ADD INDEX `time` (`trade_time`) USING BTREE ;

/**
 * 2019-11-01 upload表增加索引
 */
ALTER TABLE `t_order_upload` ADD UNIQUE ( `out_trade_no` ) USING BTREE;

/**
 * 2019年11月5日修改extend_area字段长度
 */
ALTER TABLE `t_exception_handling_record`
MODIFY COLUMN `extend_area`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '扩展字段' AFTER `bill_source`;

/**
 * 2019年11月14 t_trade_check_follow_deal表新增字段
 */
ALTER TABLE `t_trade_check_follow_deal`
  ADD COLUMN `rec_his_id` VARCHAR(10) NULL COMMENT 'His记录Id' AFTER `pat_type`,
  ADD COLUMN `rec_thrid_id` VARCHAR(10) NULL COMMENT '渠道记录Id' AFTER `rec_his_id`;

/**
 * 2019年11月28 t_healthcare_his、t_healthcare_official、t_health_exception 表新增字段  隔日平账标志  true：隔日账平   false：未平
 */
ALTER TABLE `t_healthcare_his`
  ADD COLUMN `cross_day_rec` VARCHAR(20) NULL COMMENT '隔日平账标志  true：隔日账平   false：未平' AFTER `patient_name`;
ALTER TABLE `t_healthcare_official`
  ADD COLUMN `cross_day_rec` VARCHAR(20) NULL COMMENT '隔日平账标志  true：隔日账平   false：未平' AFTER `patient_name`;
ALTER TABLE `t_health_exception`
  ADD COLUMN `cross_day_rec` VARCHAR(20) NULL COMMENT '隔日平账标志  true：隔日账平   false：未平' AFTER `shop_flow_no`;
  
  /**
 * 2019年12月17 t_trade_check_follow_deal表更改bill_source字段长度为20（之前为10）
 */
ALTER TABLE `t_trade_check_follow_deal`
  MODIFY COLUMN `bill_source`  VARCHAR(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '支付来源' AFTER `pay_type`;
  /**
   * 2019年12月23日  异常上送表
   */
 CREATE TABLE `t_order_abnormal_uplode` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `org_code` varchar(32) DEFAULT NULL COMMENT '机构编码',
  `out_trade_no` varchar(64) DEFAULT NULL COMMENT '业务系统订单号（支付发起下单的订单号:也叫商户订单号）',
  `tsn` varchar(64) DEFAULT NULL COMMENT '微信支付宝订单流水号',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '交易金额（单位元，保留2位小数，支付为正数、退款为负数）',
  `trade_date_time` varchar(20) DEFAULT NULL COMMENT '交易日期时间（yyyy-MM-dd HH:mm:ss）',
  `pay_type` varchar(4) DEFAULT NULL COMMENT '支付类型',
  `bill_source` varchar(8) DEFAULT NULL COMMENT '账单来源',
  `visit_number` varchar(64) DEFAULT NULL COMMENT '患者就诊卡号',
  `cust_name` varchar(20) DEFAULT NULL COMMENT '患者姓名',
  `terminal_number` varchar(64) DEFAULT NULL COMMENT '终端号',
  `cashier` varchar(64) DEFAULT NULL COMMENT '收费员/设备编码',
  `order_state_remark` varchar(512) DEFAULT NULL COMMENT '异常原因信息',
  `his_order_no` varchar(64) DEFAULT NULL COMMENT 'HIS流水号:退费异常时必传',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `orgCodeIndex` (`org_code`) USING BTREE,
  KEY `tsnIndex` (`tsn`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
/**
 * 渠道与终端号关联表
 */
CREATE TABLE `t_billsource_termno` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `org_code` varchar(32) NOT NULL COMMENT '机构编码',
  `bill_source` varchar(32) NOT NULL COMMENT '渠道名称',
  `term_no` varchar(32) DEFAULT NULL COMMENT '终端号',
  `pay_type` varchar(32) DEFAULT NULL COMMENT '支付类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  