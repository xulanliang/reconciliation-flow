package com.yiban.framework.account.common;

import java.util.HashMap;

/**
 * properties配置key值
 */
public class ProConstants {


    public static final String juheRefundUrl="juhe.refund.url";
    public static final String juheSignKey="sign.key";
	public static final String juheBillQuery="juhe.bill.query";
	public static final String tsnWechatBillUrl = "tsn.wechat.bill.url";
	public static final String tsnAlipayBillUrl = "tsn.alipay.bill.url";
	public static final String juhebillPath = "juhe.bill.path";
	public static final String tradeDetailPullButtonDisplay = "tradeDetail.pullButton.display";
	public static final String subsystemLoginSignUrl = "subsystem.login.sign.url";
	public static final String findOnlineOrderFlag = "find.online.order.flag";
	public static final String emailbillDate = "emailbill.date";
	public static final String yibanProjectid = "yiban.projectid";
	public static final String applicationUpload = "application.upload";
	public static final String payCenterUrl = "pay.center.url";
	//智慧服务平台交付的信息
	public static final String newPayCenterUrl = "new.pay.center.url";
	public static final String zhfwAppKey = "zhfw.appkey";
	public static final String zhfwAppSecret = "zhfw.appsecret";
	//智慧服务平台交付的信息结束
	public static final String payCenterSignUrl = "pay.center.sign.url";
	public static final String payServerAppkey = "pay.server.appkey";
	public static final String payServerAppSecret = "pay.server.appSecret";
	public static final String payServerMchAppId = "pay.server.mchAppId";
	public static final String healthAmountTypeKey = "healthAmountTypeKey";
	public static final String retryCount = "retry.count";
	public static final String retryTime = "retry.time";
	public static final String refundCount = "refund.count";
	public static final String hisOrderType = "his.order.type";
	public static final String hisOrderHttpUrl = "his.order.http.url";
	public static final String hisOrderHttpType = "his.order.http.type";
	public static final String hisOrderHttpParam = "his.order.http.param";
	public static final String hisOrderDatasourceIp = "his.order.datasource.ip";
	public static final String hisOrderDatasourcePort = "his.order.datasource.port";
	public static final String hisOrderDatasourceUsername = "his.order.datasource.username";
	public static final String hisOrderDatasourcePassword = "his.order.datasource.password";
	public static final String hisOrderDatasourceDatabase = "his.order.datasource.database";
	public static final String hisOrderDatasourceSql = "his.order.datasource.sql";
	public static final String hisOrderWebserviceUrl = "his.order.webservice.url";
	public static final String hisOrderWebserviceFunctionName = "his.order.webservice.function-name";
	public static final String hisOrderWebserviceParam = "his.order.webservice.param";
	public static final String hisHttpUrl = "his.http.url";
	public static final String payCenterOrderInfo = "pay.center.orderInfo";
	public static final String healthAmountTypeKeySql = "healthAmountTypeKeySql";
	public static final String srHealthCustom = "sr.health.custom";
	public static final String healthAmountTypeName = "healthAmountTypeName";
	public static final String donetBillUrl = "do-net.bill.url";
	public static final String billDataParseDeleteFlag = "bill.data.parse.delete.flag";
	public static final String autoRecCrossDayFlag = "autoRec.crossDay.flag";
	public static final String tangduHisSettlementOrderUrl = "tangdu.his.settlement.order.url";
	public static final String electronicRecDetailButtonOnly = "electronicRec.detailButton.only";
	public static final String payRefundUrl = "pay_refund_url";
	public static final String cardUrl = "card.url";
	public static final String tangduHisbillFirst = "tangdu.hisbill.first";
	public static final String hisAdd = "his.add";
	public static final String hisUser = "his.user";
	public static final String hisPass = "his.pass";
	public static final String hisDriverName = "his.driverName";
	public static final String crossDayReconciliation = "crossDayReconciliation";
	public static final String stepNum = "stepNum";
	public static final String offsetStepNum = "offsetStepNum";
	public static final String ccbEmailFilepath = "ccb.email.filepath";
	public static final String misEmailFilepath = "mis.email.filepath";
	public static final String ccbEmailUsername = "ccb.email.username";
	public static final String ccbEmailPassword = "ccb.email.password";
	public static final String ccbEmailHost = "ccb.email.host";// pop3.163.com
	public static final String ccbEmailPort = "ccb.email.port";// 110
	public static final String ccbEmailType = "ccb.email.type";// pop3
	public static final String ccbEmailFrom = "ccb.email.from";
	public static final String misEmailFrom = "mis.email.from";
	public static final String ccbEmailValidate = "ccb.email.validate";
	public static final String ccbEmailSubject = "ccb.email.subject";
	public static final String misEmailSubject = "mis.email.subject";
	public static final String ccbEmailFolder = "ccb.email.folder";
	public static final String bankFtpHost = "bank.ftp.host";
	public static final String bankFtpPort = "bank.ftp.port";// 21
	public static final String bankFtpUsername = "bank.ftp.username";
	public static final String bankFtpPassword = "bank.ftp.password";
	public static final String bankFtpPath = "bank.ftp.path";
	public static final String bankFtpIsFtps = "bank.ftp.isftps";
	public static final String bankBillPath = "bank.bill.path";
	//兴义贵州银行ftp账单路径
	public static final String xingyi_guizhou_bankFtpPath = "xingyi.guizhou.bank.ftp.path";
	public static final String xingyi_guizhou_bankBillPath = "xingyi.guizhou.bank.bill.path";
	public static final String xingyi_guizhou_fileName = "xingyi.guizhou.fileName";
	public static final String jhzfHttpUrl = "jhzf.http.url";
	public static final String hisWebserviceUrl = "his.webservice.url";
	public static final String outreachPlatformUserName = "outreach.platform.user.name";
	public static final String outreachPlatformToken = "outreach.platform.token";
	public static final String outreachPlatformMethodName = "outreach.platform.method.name";
	public static final String hisAppId = "his.appid";
	public static final String hisTransCode = "his.trans.code";
	public static final String hisPayType = "his.pay.type";
	public static final String hisStandardWebserviceUrl="his.standard.webservice.url";
	public static final String honganToken = "hongan.token";
	public static final String honganKey = "hongan.key";
	public static final String honganService = "hongan.service";
	public static final String kmyaZytUrl = "kmya.zyt.url";
	public static final String szeyhisEndpoint = "szeyhis.endpoint";
	public static final String jkszEndpoint = "jksz.endpoint";
	public static final String jkszDownUrl = "jksz.downUrl";
	public static final String yxwEndpoint = "yxw.endpoint";
	public static final String yxwParserMethod = "yxw.parser.method";
	public static final String yxwNameSpace = "yxw.nameSpace";
	public static final String yxwSer = "yxw.ser";
	public static final String yxwResponseType = "yxw.responseType";
	public static final String yxwOrderMode = "yxw.orderMode";
	public static final String yyAliEndpoint = "YyAli.endpoint";
	public static final String bocExeUrl = "boc-exe-url";
	public static final String bocExeResultUrl = "boc-exe-result-url";
	public static final String lykjEndpoint = "lykj.endpoint";
	public static final String lykjPayMethod = "lykj.pay.method";
	public static final String jkszPaDctorsftpIP = "jksz.paDctorsftpIP";
	public static final String tangduEndpoint = "tangdu.endpoint";
	public static final String tangduUsername = "tangdu.username";
	public static final String tangduPassword = "tangdu.password";
	public static final String tangduMethod = "tangdu.method";
	public static final String tangduRefundMethod = "tangdu.refund.method";
	public static final String tdFtpHost = "td.ftp.host";
	public static final String tdFtpUsername = "td.ftp.username";
	public static final String tdFtpPassword = "td.ftp.password";
	public static final String tdFtpPort = "td.ftp.port";
	public static final String tdFtpPath = "td.ftp.path";
	public static final String tianjinServiceURL = "tianjin.serviceURL";
	public static final String tmCsvDataPath = "tm.csvDataPath";
	public static final String jdWeChatHttpUrl = "jdWeChat.http.url";
	public static final String apiSalt = "api.salt";
	public static final String bukaAdd = "buka.add";
	public static final String bukaDriverName = "buka.driverName";
	public static final String bukaUser = "buka.user";
	public static final String bukaPass = "buka.pass";
	public static final String machineNumber = "machine.number";
	public static final String misPosJdbcUrl = "mis-pos.jdbc.url";
	public static final String misPosDriverClass = "mis-pos.driver.class";
	public static final String misPosUsername = "mis-pos.username";
	public static final String misPosPassword = "mis-pos.password";
	public static final String xmlNodes = "xml.nodes";
	public static final String hisUrl = "his.url";
	public static final String juheAcount = "juhe.acount";
	public static final String juheGetFileNameUrl = "juhe.get.file.name.url";
	public static final String juheDownloadUrl = "juhe.download.url";
	public static final String juheSharePath = "juhe.share.path";
	public static final String juheDownloadPath = "juhe.download.path";
	public static final String juheIpPath = "juhe.ip.path";
	public static final String juheUserName = "juhe.userName";
	public static final String juhePassWord = "juhe.passWord";
	public static final String juheGetType = "juhe.getType";
	public static final String channelUrl = "channel.url";
	public static final String zryhJhRefundUrl = "zryh.jh.refund.url";
	public static final String zryhJhAccount = "zryh.jh.account";
	public static final String zryhRefundUrl = "zryh.refund.url";
	public static final String appConfigRemoteUi = "app.config.remoteUi";
	public static final String appConfigName = "app.config.name";
	public static final String appConfigVersion = "app.config.version";
	public static final String appConfigStartYear = "app.config.startYear";
	public static final String appConfigPrefix = "app.config.prefix";
	// 中日友好芸泰渠道信息
	public static final String yuntaiIsvId = "yuntai.isvId";
	public static final String yuntaiPartnerId = "yuntai.partnerId";
	public static final String yuntaiIsvPrivteKey = "yuntai.isvPrivteKey";
	public static final String yuntaiUppPublicKey = "yuntai.uppPublicKey";
	// 线程休眠秒数
	public static final String threadSleepSeconds = "thread.sleep.seconds";

	// 拉账单相关参数
	public static final String systemCode = "system.code";
	// 多码合一时的systemCode
	public static final String systemCodeUnified = "system.code.unified";

	public static final String wnjhzfRefundUrl = "wnjhzf.refund.url";
	public static final String wnjdRefundUrl = "wnjd.refund.url";
	// 电子对账页面的数据来源选项是否显示：默认显示
	public static final String electronicRecPatTypeDisplay = "electronicRec.pattype.display";
	// 支付渠道交易明细页面的数据汇总是否显示：默认不显示
	public static final String thirdtradeSummaryDisplay = "thirdtrade.summary.display";

	// 定时器类型常量
	public static final String rechannelJobTime = "rechannel.job.time";
	public static final String autoRecJobTime = "autorec.job.time";
	public static final String emailbillTime = "emailbill.job.time";
	public static final String billParseTime = "billParse.job.time";
	public static final String normalRefundTime = "normalRefund.job.time";
	public static final String exceptionRefundTime = "exceptionRefund.job.time";
	public static final String emailBillDownloadTime = "email.bill.download.time";
	public static final String hisJobTime = "his.job.time";
	public static final String hisSettlementJobTime = "his.settlement.job.time";
	public static final String hisTradeDetailJobTime = "his.tradedetail.job.time";

	//黄冈病案
	public static final String binganBillUrl = "his.bingan.bill.url";
	public static final String binganBillKey = "his.bingan.bill.url.apikey";

	//连云港互联网渠道信息
	public static final String netWorkHttpUrl = "network.http.url";
	public static final String netWorkHttpChannelUrl = "network.http.channel.url";

	// 泰康互联网医院中台服务，响应加签信息
	public static final String TaiKang_MS_url = "taikang_ms_url";
	public static final String TaiKang_MS_appID = "taikang_ms_appId";
	public static final String TaiKang_MS_hospital_code = "taikang_ms_hospital_code";
	public static final String TaiKang_MS_app_secret = "taikang_ms_app_secret";
	public static final String TaiKang_POS_Terminal_Num_Arr = "taikang_terminal_num";

	// 深圳市二参数配置信息
	public static final String SZSE_PAY_TERM_NO_CWK = "szse_pay_term_no_cwk";
	public static final String SZSE_PAY_TERM_ZY = "szse_pay_term_no_zy";
	
	// 武进区域
	public static final String WUJIN_CASHIER_LIST = "wujin_cashier_list";
	public static final String WUJIN_HIS_ORG_CODE= "wujin_his_org_code";

	// 武进洛阳
	public static final String WUJIN_LUOYANG_CASHIER_LIST = "wujin_luoyang_cashier_list";
	public static final String WUJIN_LUOYANG_HIS_ORG_CODE= "wujin_luoyang_his_org_code";

	// 武进前黄
	public static final String WUJIN_QIANHUANG_CASHIER_LIST = "wujin_qianhuang_cashier_list";
	public static final String WUJIN_QIANHUANG_HIS_ORG_CODE= "wujin_qianhuang_his_org_code";

	// 武进横林
	public static final String WUJIN_HENGLIN_CASHIER_LIST = "wujin_henglin_cashier_list";
	public static final String WUJIN_HENGLIN_HIS_ORG_CODE= "wujin_henglin_his_org_code";

	// 武进雪堰
	public static final String WUJIN_XUEYAN_CASHIER_LIST = "wujin_xueyan_cashier_list";
	public static final String WUJIN_XUEYAN_HIS_ORG_CODE= "wujin_xueyan_his_org_code";


	// 登录是否有短信验证码
	public static final String MESSAGE_VERIFY_CODE = "message.verify.code";
	// 短信模板
	public static final String MESSAGE_TEMPLATE = "message.template";
	// 验证码有效期
	public static final String MESSAGE_VERIFY_CODE_TIME = "message.verify.code.time";
	// 短信验证码接口地址
	public static final String MESSAGE_VERIFY_CODE_URL = "message.verify.code.url";
	// 获取短信验证码频率限制
	public static final String MESSAGE_VERIFY_CODE_TIME_LIMIT = "message.verify.code.time.limit";

	// 红十字中心医院
	public static final String transCode = "TransCode";
	public static final String tsnOrderNo = "tsnOrderNo";

	// 公共配置的一些默认值， 医院个性化的默认值不配置在这里
	public static final HashMap<String, String> DEFAULT = new HashMap<>();
	
	// 拉取账单周期，拉几天前的账单
	public static final String billParseCycle = "bill.parse.cycle";
	
	public static final String hisblbUrl="his.blb.url";
	
	//皖北煤电总院配置
	public static final String hisOracleViewDriver="his.oracle.view.driver";//his视图驱动
	public static final String hisOracleViewAddress="his.oracle.view.address";//his视图地址
	public static final String hisOracleViewUser="his.oracle.view.user";//his视图用户名
	public static final String hisOracleViewpass="his.oracle.view.password";//his视图密码
	public static final String hisOracleViewSqlWZ="his.oracle.view.sql.wz";//his视图微信and支付宝sql
	public static final String hisOracleViewSqlYH="his.oracle.view.sql.yh";//his视图银行卡sql
	public static final String stMysqlViewDriver="st.mysql.view.driver";//食堂视图驱动
	public static final String stMysqlViewAddress="st.mysql.view.address";//食堂视图地址
	public static final String stMysqlViewUser="st.mysql.view.user";//食堂视图用户名
	public static final String stMysqlViewpass="st.mysql.view.password";//食堂视图密码
	public static final String stMysqlViewSql="st.mysql.view.sql";//食堂视图sql
	public static final String tjMysqlViewDriver="tj.mysql.view.driver";//体检视图驱动
	public static final String tjMysqlViewAddress="tj.mysql.view.address";//体检视图地址
	public static final String tjMysqlViewUser="tj.mysql.view.user";//体检视图用户名
	public static final String tjMysqlViewpass="tj.mysql.view.password";//体检视图密码
	public static final String tjMysqlViewSqlWZ="tj.mysql.view.sql.wz";//体检视图微信and支付宝sql
	public static final String tjMysqlViewSqlYH="tj.mysql.view.sql.yh";//体检视图银行卡sql
	
	//广水
	public static final String hisWSToken="guagnshui.webservice.token";//广水webservice令牌
	public static final String gsWSPassword="guagnshui.webservice.password";//广水webservice数据加密密钥
	
	//漯河市中心医院
	public static final String sqlServerDriverName="sqlServer.driver.name";
	public static final String sqlServerUrl="sqlServer.driver.url";
	public static final String sqlServerUserName="sqlServer.driver.userName";
	public static final String sqlServerPassword="sqlServer.driver.password";
	
	static {
		DEFAULT.put(hisStandardWebserviceUrl, "http://localhost:8000/his");
		DEFAULT.put(subsystemLoginSignUrl, "http://sso-demo.clear-sz.com/sso/sys/user/login-status");
		DEFAULT.put(payCenterUrl, "http://pay.clearofchina.com");
		DEFAULT.put(payServerAppkey, "");
		DEFAULT.put(payServerMchAppId, "");
		DEFAULT.put(retryCount, "3");
		DEFAULT.put(retryTime, "15");
		DEFAULT.put(refundCount, "10");
		DEFAULT.put(hisOrderType, "http");
		DEFAULT.put(hisOrderHttpType, "get");
		DEFAULT.put(payCenterOrderInfo, "false");
		DEFAULT.put(billDataParseDeleteFlag, "true");
		DEFAULT.put(autoRecCrossDayFlag, "false");
		DEFAULT.put(electronicRecDetailButtonOnly, "false");
		DEFAULT.put(cardUrl, "http://192.168.27.184:8090/order/card");
		DEFAULT.put(tangduHisbillFirst, "true");
		DEFAULT.put(findOnlineOrderFlag, "true");
		DEFAULT.put(rechannelJobTime, "12:00:00");
		DEFAULT.put(billParseTime, "11:00:00");
		DEFAULT.put(hisJobTime, "09:30:00");
		DEFAULT.put(autoRecJobTime, "11:20:00");
		DEFAULT.put(crossDayReconciliation, "false");
		DEFAULT.put(stepNum, "0");
		DEFAULT.put(offsetStepNum, "0");
		DEFAULT.put(ccbEmailFilepath, "D:\\billfile");
		DEFAULT.put(ccbEmailHost, "pop3.163.com");
		DEFAULT.put(ccbEmailPort, "110");
		DEFAULT.put(ccbEmailType, "pop3");
		DEFAULT.put(ccbEmailValidate, "true");
		DEFAULT.put(ccbEmailFolder, "INBOX");
		DEFAULT.put(healthAmountTypeKeySql,
				"cost_all,cost_basic,cost_account,cost_cash,cost_whole,cost_rescue,cost_subsidy");
		DEFAULT.put(healthAmountTypeKey, "costAll,costBasic,costAccount,costCash,costWhole,costRescue,costSubsidy");
		DEFAULT.put(healthAmountTypeName, "医疗总费用,基本医疗费用,账户支付金额,现金支付金额,统筹支付金额,大病救助基金支付,公务员补助支付");
		DEFAULT.put(appConfigRemoteUi, "false");
		DEFAULT.put(appConfigName, "综合支付平台");
		DEFAULT.put(appConfigVersion, "1.7.0");
		DEFAULT.put(appConfigStartYear, "2019");
		DEFAULT.put(appConfigPrefix, "rec_hospital");
		DEFAULT.put(electronicRecPatTypeDisplay, "false");
		DEFAULT.put(systemCode, "51");
		DEFAULT.put(thirdtradeSummaryDisplay, "false");
		DEFAULT.put(threadSleepSeconds, "120");
		DEFAULT.put(MESSAGE_VERIFY_CODE, "false");
		DEFAULT.put(MESSAGE_VERIFY_CODE_TIME, "3");
		DEFAULT.put(MESSAGE_VERIFY_CODE_TIME_LIMIT, "0");
	}

	public static final HashMap<String, String> TYPE = new HashMap<>();
	static {
		TYPE.put("common", "公共");
		TYPE.put("private", "医院");
	}
	public static final HashMap<String, String> MODEL = new HashMap<>();
	static {
		MODEL.put("normal", "普通模式");
		MODEL.put("loop", "循环模式");
		MODEL.put("timer", "定时模式");
	}
}
