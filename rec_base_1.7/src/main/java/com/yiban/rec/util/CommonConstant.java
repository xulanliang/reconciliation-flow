package com.yiban.rec.util;

public interface CommonConstant {
	/**
	 * 默认初始化密码.
	 */
	String DEFAULT_INIT_PASSWORD = "123456";
	
	//mq连接状态 1:连接
	String MQ_ISCONNECT = "1";

	static final String T_REC_PAY_RESULT = "t_rec_pay_result";
	
	static final String T_REC_HIS_TRANSACTION = "t_rec_histransactionflow";

	static final String T_REC_PLATFORMFLOW_LOG = "t_rec_platformflow_log";

	static final String T_REC_THRID_BILL = "t_thrid_bill";
	
	static final String T_REC_CASH = "t_rec_cash";
	
	static final String T_HEALTHCARE_OFFICIAL = "t_healthcare_official";
	
	static final String T_HEALTHCARE_HIS = "t_healthcare_his";
	
	static final Long ALL_ID = 9999L;//页面初始化默认“全部”标记
	static final String DEVICE_NO = "全部";//页面初始化默认设备“全部”标记
	
	static final String CODING_FORMAT = "utf-8";
	
	static final String CODING_FORMAT_GBK = "GBK";
	
	static final String BUSSINESS_TYPE = "交易";
	static final String BUSSINESS_TYPE_PAY = "支付";
	static final String BUSSINESS_TYPE_REFUND = "退费";
	
	//导出默认条数
	static final int exportPageSize = 5000;
	
	//his交易明细数据队列
	public static final String HISTOCENTER_PLATFORM_MQ = "hisToCenter_his";
	
	//支付渠道数据明细
	public static final String HISTOCENTER_THIRD_MQ = "hisToCenter_third";
	
	//平台数据
	public static final String HISTOCENTER_HIS_MQ = "hisToCenter_platform";
	
	//his上传中心确认消息队列
	public static final String HISTOCENTER_CONFIRM_MQ = "hisToCenter_confirm";
	
	//消息上传通道测试
	public static final String HISTOCENTER_MESSAGEUPLOAD_MQ = "hisToCenter_messageupload";
	
	//放射沙龙上传数据消息队列
	public static final String RADIATION_MESSAGE_MQ = "radiation_message_mq";
	
	//体检服务平台
	public static final String TJ_MESSAGE_MQ = "tj_message_mq";
	public static final String HEAR_BEAT_QUEUE="rec_hearbeat";
	
	//中心到his消息确认
	public static final String CENTERTOHIS_BILLSTATUS_MQ = "centerToHis_billStatus";
	/**
	 * 对账系统定时任务taskId
	 */
	// 获取his渠道账单任务id测试
	public static final String REC_JOB_ID_GET_CHANNEL_ORDER_TEST = "REC_JOB_ID_GET_CHANNEL_ORDER";
	// 获取his渠道账单任务id
	public static final String REC_JOB_ID_GET_CHANNEL_ORDER = "REC_JOB_ID_GET_CHANNEL_ORDER";
	// 获取his渠道账单任务失败时重新获取id
	public static final String REC_JOB_ID_RE_GET_CHANNEL_ORDER = "REC_JOB_ID_RE_GET_CHANNEL_ORDER";
	// his上传数据到中心
	public static final String REC_JOB_ID_HIS_TO_CENTER = "REC_JOB_ID_HIS_TO_CENTER";

	// his上传数据到中心失败从新上传
	public static final String REC_JOB_ID_RE_HIS_TO_CENTER = "REC_JOB_ID_RE_HIS_TO_CENTER";
	// 中心把账单入库状态告知HIS
	public static final String REC_JOB_ID_CENTER_TO_HIS_BILL_STATUS = "REC_JOB_ID_CENTER_TO_HIS_BILL_STATUS";
	
	//自动解析对账程序
	public static final String REC_JOB_ID_AUTO_TNALYLIS_BILL = "REC_JOB_ID_AUTO_TNALYLIS_BILL";
	
	//自动对账
	public static final String REC_JOB_ID_AUTO_REC = "REC_JOB_ID_AUTO_REC";
	
	//自动服务监测
	public static final String AUTO_CENTER_SERVICE_MONITOR = "AUTO_CENTER_SERVICE_MONITOR";
	
	//线上数据传线下
	public static final String ONLINE_TO_OFFLINE_ID = "ONLINE_TO_OFFLINE_ID";
	
	//自动对账定时任务
	public static final String AUTO_REC_TASK_ID = "AUTO_REC_TASK_ID";
	
	//微信支付账单
	public static final String WEICHAT_PAY_FILE = "TRADE";
	
	public static final String WEICHAT_PAY_FILE_MIN = "Trade";
	
	//微信退款账单
	public static final String WEICHAT_REFUND_FILE = "REFUND";
	
	//支付宝支付账单
	public static final String ALIPAY_PAY_FILE = "卖出交易";
	
	//支付宝退款账单
	public static final String ALIPAY_REFUND_FILE = "退款交易";
	
	//调用银医交易成功编码
	public static final String TRADE_CODE_SUCCESS = "0050";
	
	//银医退款交易编码
	public static final String TRADE_CODE_REFUND = "02004";
	
	//账单交易码
	public static final String TRADE_VALUE_WAITPAY = "待买家支付";
	public static final String TRADE_VALUE_REFUND = "退款中";
	public static final String TRADE_VALUE_STATE = "关闭";
	
	public static final String TRADE_VALUE_WAIT_PAY = "待付款";
	
	//退费标记
	public static final String REFUND_FLAG = "zhzf";
	
	//当日账单交易交易编码
	public static final String NOW_BILL_CHECK = "00003";
	
	//现金账单支付类型code
	public static final String CASH_BILL_PAY_CODE = "0049";
	
	//开始时间标记
	public static final String START_TIME_FLAG = " 00:00:00";
	
	//介绍时间标记
	public static final String END_TIME_FLAG = " 23:59:59";
	
	//服务监测基础队列
	public static final String SERVICE_MONITOR_QUEUE = "service_monitor_queue";
	
	//线上数据传线下队列
	public static final String ONLINE_TO_OFFLINE_QUEUE = "online_to_offline_queue";
	
	//调用深圳市第二人民医院HIS账单接口交易成功编码
	public static final String SZSR_TRADE_CODE_SUCCESS = "0";//成功
	
	public static final String SZSR_YB_CODE_SUCCESS="00000000";//成功
	
	public static final String SZSR_YXW_TRADE_CODE_SUCCESS = "1";//医享网账单拉取成功标志
	
	public static final String SZSR_LYKJ_TRADE_CODE_SUCCESS = "0";//健康160账单拉取成功标志
	
	public static final String SZSR_YYZFB_TRADE_CODE_SUCCESS = "SUCCESS";//健康160账单拉取成功标志
	
}
