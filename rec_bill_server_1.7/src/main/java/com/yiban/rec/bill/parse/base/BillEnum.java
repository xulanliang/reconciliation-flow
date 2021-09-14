package com.yiban.rec.bill.parse.base;

import org.apache.commons.lang.StringUtils;

public enum BillEnum {
	PLATFORM_TYPE_HIS(1, "机构"),//
	PLATFORM_TYPE_THIRD(2, "第三方支付平台"),
	PLATFORM_TYPE_HIS_TO_CENTER(3, "HisToCenter"),
	PLATFORM_TYPE_HIS_AND_THIRD(4, "机构和第三方支付平台"),

	NETWORK_STATE_YES(119, "0079", "连接"),
	NETWORK_STATE_NO(120, "0179", "未连接"),

	TRADE_CODE_PAY(99, "01011", "消费"),
	TRADE_CODE_REVERSAL(100, "01012", "冲正"),
	TRADE_CODE_BAR(109, "02001", "条码支付"),
	TRADE_CODE_REVOKE(101, "01013", "撤销"),
	TRADE_CODE_RETURN(102, "01014", "退货"),
	TRADE_CODE_RREORDER(108, "02000", "预下单"),
	TRADE_CODE_REFUND(112, "02004", "退款"),
	TRADE_CODE_CLOSED(114, "02006", "关闭订单"),
	HANDLE_SUCCESS(71, "成功"),
	HANDLE_FAIL(72, "失败"),
	HANDLE_NO_DATA(150, "无数据"),

	PAY_CODE(108, "0156", "缴费"),
	REFUND_CODE(108, "0256", "退费"),

	DETELE_FALG(0, "删除标记"),
	ACTIVE_FALG(1, "是否有效标记"),
	NOCASH_PAYTYPE(430, "no_cash", "非现金支付类型"),
	ORDER_STATE_UNKNOWN(115, "0051", "未知的订单状态"),
	ORDER_STATE_FAIL(116, "8202", "未支付"),
	REC_SINGLE_NO(0, "单边账标记(不存在单边账)"),
	//支付类型
	CASH_PAYTYPE(43, "0049", "现金"),
	PAY_TYPE_WECHAT(45, "0249", "微信"),
	PAY_TYPE_ALIPAY(46, "0349", "支付宝"),
	PAY_TYPE_AGGREGATE(47, "1649", "聚合支付"),
	PAY_TYPE_WJYZT(48, "2649", "武进区一账通"),
	PAY_TYPE_UNIONPAY(49, "3649", "云闪付"),
	PAY_TYPE_HEALTH(47, "0559", "医保支付"),
	PAY_TYPE_JDWECHAT(49, "jdwechat", "金蝶微信"),
	PAY_TYPE_BANK(44, "0149", "银行卡"),
	//渭南定制
	PAY_TYPE_CCBBANK(44, "ccbbank", "建设银行卡"),
	PAY_TYPE_PFBANK(44, "pfbank", "浦发银行卡"),
	//渭南定制结束
	PAY_TYPE_HEALTHCARE(49, "0449", "医保"),
	PAY_TYPE_QT(49, "9949", "其他"),
	// 农行掌银
	PAY_TYPE_NHZY(50, "0848", "农行掌银支付"),
	// 工行自助机
	PAY_TYPE_ICBCZZ(51, "1550", "工行自助机"),

	PAY_CHANNEL_WECHAT(78, "weixin", "微信"),
	PAY_CHANNEL_ALIPAY(79, "zhifubao", "支付宝"),//支付渠道
	PAY_PAY_SOURCE(49, "Pay_Source", "账单来源"),//账单来源
	ORG_NO_TEMP(21, "5307601", "机构编码值"),
	ORG_NO_TEMP_TWO(27, "5307607", "机构编码值"),
	ORDER_STATE_SUCCUSS(117, "成功"),
	PAY_BUSINESS_REGISTER(58, "0451", "成功"),
	REC_TYPE_TWO(191, "two_rec", "两方对账"),
	REC_TYPE_THREE(191, "three_rec", "三方对账"),
	BILL_SOURCE_JTYH(0, "jtyh", "交通银行"),
	BILL_SOURCE_SELF(0, "self", "银医"),
	BILL_SOURCE_SELF_JD(0, "self_jd", "巨鼎"),
	BILL_SOURCE_ZSYY_QLC(0,"zsyy","掌上医院(全流程)"),
	BILL_SOURCE_HIS_CK(0,"his_ck","HIS窗口"),
	BILL_SOURCE_CSCF(0,"cscf","唱收唱付屏幕"),
	BILL_SOURCE_MZ(0,"bill_source_mz","门诊"),
	BILL_SOURCE_ZY(0,"bill_source_zy","住院"),
	BILL_SOURCE_SELF_TD_JD(0, "self_td_jd", "唐都金蝶"),
	BILL_SOURCE_PAJK(0, "8389", "深圳市二平安好医生"),
	BILL_SOURCE_JHZZ(0, "jhzz", "深圳市二建行自助机"),
	BILL_SOURCE_YBAP(0, "8386", "深圳市二云医支付宝"),
	BILL_SOURCE_JKSZ(0, "8388", "深圳市二健康深圳"),
	BILL_SOURCE_JY160(0, "8387", "深圳市二健康160"),
	BILL_SOURCE_HIS(0, "8301", "深圳市二His窗口"),
	BILL_SOURCE_ZSYY(0, "8381", "深圳市二医享网"),
	BILL_SOURCE_DZSB(0, "dzsb", "深圳市二电子社保卡"),
	BILL_SOURCE_NLJK(0, "nljk", "深圳市二_纳里健康"),
	BILL_SOURCE_ZZGH(0, "8391", "深圳市二中行"),
	BILL_SOURCE_ICBC(0, "icbc", "工行"),
	BILL_SOURCE_JHJH(0, "jhjh", "仙桃第一人民医院建行聚合支付"),
	BILL_SOURCE_XTBK(0, "buka", "仙桃第一人民医院补卡"),
	BILL_SOURCE_SJD(0, "sjd", "仙桃第一人民医院掌上银行"),
	BILL_SOURCE_XXCK(0, "xxck", "仙桃第一人民医院HIS窗口"),
	BILL_SOURCE_CWPAJK(0, "cwpajk", "长武平安好医生"),
	BILL_SOURCE_ZSYYWX(0, "zsyywx", "掌上医院（医享网-微信公众号）"),
	BILL_SOURCE_YBZF(0, "ybzu", "医保支付（云医支付宝-支付宝生活号）"),
	BILL_SOURCE_ZUGH(0, "zugh", "自助挂号（中行自助机)"),
	BILL_SOURCE_NYKJ(0, "nykj", "宁远科技（就医160）"),
	BILL_SOURCE_ZHEJIANG(0, "zhejiang", "浙大附医 微信、支付宝"),
	BILL_SOURCE_CWJKSZ(0, "cwjksz", "长武健康深圳"),
	BILL_SOURCE_SPDB(0, "spdb", "浦发银行"),
	BILL_SOURCE_WNJD(0, "wnjd", "渭南金蝶"),
	BILL_SOURCE_WNTW(0, "wntw", "渭南天网"),
	BILL_SOURCE_WNCK(0, "wnck", "渭南窗口"),
	BILL_SOURCE_WNWY(0, "wnwy", "渭南微医"),
	BILL_SOURCE_WNQT(0, "wnqt", "渭南未知渠道"),
	BILL_SOURCE_WNPF(0, "wnpf", "渭南浦发"),
	BILL_SOURCE_WNJH(0, "wnjh", "渭南建行"),
	BILL_SOURCE_HISCK(0, "his_ck", "HIS窗口"),
	BILL_SOURCE_TK_WX(0, "his_tk_wx", "泰康微信"),
	BILL_SOURCE_BLB(0, "blb", "病历本发放机"),
	BILL_SOURCE_JHZY(0, "jhzy", "建行住院"),
	BILL_SOURCE_JHMZ(0, "jhmz", "建行门诊"),
	BILL_SOURCE_NHZY(0, "nhzy", "农行住院"),
	PAY_TYPE_WEIMAI(47,"weimai","微脉支付"),//支付类型
	BILL_SOURCE_NHMZ(0, "nhmz", "农行门诊"),
	BILL_SOURCE_NHMZ_MISPOS(0, "nhmz_mispos", "农行门诊(MISPOS)"),
	BILL_SOURCE_NHZY_MISPOS(0, "nhzy_mispos", "农行住院(MISPOS)"),
	BILL_SOURCE_JHMZ_MISPOS(0, "jhmz_mispos", "建行门诊(MISPOS)"),
	BILL_SOURCE_JHZY_MISPOS(0, "jhzy_mispos", "建行住院(MISPOS)"),
	BILL_SOURCE_JHZY_MISPOS_CK(0, "jhzy_mispos_ck", "建行住院(窗口)"),
	BILL_SOURCE_JHMZ_MISPOS_CK(0, "jhmz_mispos_ck", "建行门诊(窗口)"),
	BILL_SOURCE_WXMZ(0, "wxmz", "微信门诊"),
	BILL_SOURCE_WXZY(0, "wxzy", "微信住院"),
	BILL_SOURCE_ALIMZ(0, "alimz", "支付宝门诊"),
	BILL_SOURCE_ALIZY(0, "alizy", "支付宝住院"),
	BILL_SOURCE_HISMZCK(0,"his_mz_ck","HIS门诊窗口"),
	BILL_SOURCE_HISZYCK(0,"his_zy_ck","HIS住院窗口"),
	BILL_SOURCE_SELF_MZ(0,"self_mz","自助机门诊"),
	BILL_SOURCE_SELF_ZY(0,"self_zy","自助机住院"),
	BILL_SOURCE_SELF_SZZK(0,"szzk","苏州智康"),
	//全流程
	BILL_SOURCE_ZSYY_MZ(0,"zsyy_mz","掌上医院门诊"),
	BILL_SOURCE_ZSYY_ZY(0,"zsyy_zy","掌上医院住院"),

	BILL_SOURCE_CIB(0,"cib","兴业银行"),
	BILL_SOURCE_BOC(0,"boc","中国银行"),
	BILL_SOURCE_CCB(0,"ccb","中国建设银行"),
	BILL_SOURCE_QT(0,"qt","其它来源"),
	
	CASH_BILL_SOURCE_WNCKXJ(0, "wn_ck_xj", "渭南窗口现金"),
	CASH_BILL_SOURCE_WNNHZZJXJ(0, "wn_nhzzj_xj", "渭南农行自助机现金"),
	CASH_BILL_SOURCE_WNSELFXJ(0, "wn_self_xj", "渭南巨鼎自助机现金"),
	BILL_SOURCE_CZYG(0, "czyg", "潮州阳光"),

	// 武汉泰康
	BILL_SOURCE_TAIKANG_POS(0, "POS", "武汉泰康POS扫码"), 
	BILL_SOURCE_TAIKANG_CD(0, "CD", "武汉泰康POS刷银行卡"),
	BILL_SOURCE_TAIKANG_HIS_CK(0, "his_ck", "武汉泰康HIS窗口"), 
	BILL_SOURCE_TAIKANG_WZ(0, "未知", "未知渠道"),
	BILL_SOURCE_TAIKANG_SOURCES_SKZF(0, "刷卡支付", "POS账单字段判断条件"),
	BILL_SOURCE_TAIKANG_SOURCES_ZFBZF(0, "支付宝支付", "POS账单字段判断条件"),
	BILL_SOURCE_TAIKANG_SOURCES_WXZF(0,"微信支付","POS账单字段判断条件"),
	
	//支付业务类型
	PAY_BUSINESS_TYPE_WZ(51, "0051", "未知"),
	PAY_BUSINESS_TYPE_MZCZ(51, "0151", "门诊充值"),
	PAY_BUSINESS_TYPE_BANK(51, "0251", "办卡"),
	PAY_BUSINESS_TYPE_BUK(51, "0351", "补卡"),
	PAY_BUSINESS_TYPE_DRGH(51, "0451", "当日挂号"),
	PAY_BUSINESS_TYPE_JF(51, "0551", "缴费"),
	PAY_BUSINESS_TYPE_TF(51, "0651", "退费"),
	PAY_BUSINESS_TYPE_ZYYJJ(51, "0751", "住院预交金"),
	PAY_BUSINESS_TYPE_YYGH(51, "0851", "预约挂号"),
	PAY_BUSINESS_TYPE_BLBFF(51, "0951", "病历本发放"),
	PAY_BUSINESS_TYPE_TJ(51, "1051", "体检"),
	PAY_BUSINESS_TYPE_ZYCZ(51, "1151", "住院充值"),
	PAY_BUSINESS_TYPE_CYJS(51, "1251", "出院结算"),
	PAY_BUSINESS_TYPE_THJS(51,"1351","退号结算"),
	PAY_BUSINESS_TYPE_GHJS(51,"1451","挂号结算"),
	PAY_BUSINESS_TYPE_CX(51,"1551","冲销"),
	
	
	//患者类型
	PAT_TYPE_MZ(172, "mz", "门诊"),
	PAT_TYPE_ZY(173, "zy", "住院"),
	PAT_TYPE_QT(173, "qt", "其他"),
	PAT_TYPE_ZYMZ(191, "zymz", "总计"),

	BILL_SOURCE_THIRD(0, "third", "第三方"),
	TRADE_TYPE_PAY(0, "0156", "缴费"),
	TRADE_TYPE_REFUND(0, "0256", "退费"),
	REFUND_TYPE_UNEXAMINE(0, "1809303", "审核中"),//订单状态
	REFUND_TYPE_REJECT(0, "1809305", "已驳回"),//订单状态
	REFUND_TYPE_REFUND(0, "1809304", "已退费")//订单状态
	;


	private Integer id;
	private String value;
	private String code;

	private BillEnum(Integer id, String value, String code) {
		this.id = id;
		this.value = value;
		this.code = code;
	}

	private BillEnum(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public static BillEnum getByCode(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		for (BillEnum tmp : BillEnum.values()) {
			if (tmp.value.equals(value)) {
				return tmp;
			}
		}
		return null;
	}
}
