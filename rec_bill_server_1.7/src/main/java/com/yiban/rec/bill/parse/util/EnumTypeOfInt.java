package com.yiban.rec.bill.parse.util;

import java.util.Arrays;

public enum EnumTypeOfInt {
	PLATFORM_TYPE_HIS(1,"机构"),//
	PLATFORM_TYPE_THIRD(2,"第三方支付平台"),
	PLATFORM_TYPE_HIS_TO_CENTER(3,"HisToCenter"),
	PLATFORM_TYPE_HIS_AND_THIRD(4,"机构和第三方支付平台"),

	NETWORK_STATE_YES(119,"0079","连接"),
	NETWORK_STATE_NO(120,"0179","未连接"),

	TRADE_CODE_PAY(99,"01011","消费"),
	TRADE_CODE_REVERSAL(100,"01012","冲正"),
	TRADE_CODE_BAR(109,"02001","条码支付"),
	TRADE_CODE_REVOKE(101,"01013","撤销"),
	TRADE_CODE_RETURN(102,"01014","退货"),
	TRADE_CODE_RREORDER(108,"02000","预下单"),
	TRADE_CODE_REFUND(112,"02004","退款"),
	TRADE_CODE_CLOSED(114,"02006","关闭订单"),
	HANDLE_SUCCESS(71,"成功"),
	HANDLE_FAIL(72,"失败"),
	HANDLE_NO_DATA(150,"无数据"),

	PAY_CODE(108,"0156","缴费"),
	REFUND_CODE(108,"0256","退费"),
	REVOKE_CODE(108,"0356","撤销"),

	DETELE_FALG(0,"删除标记"),
	ACTIVE_FALG(1,"是否有效标记"),
	NOCASH_PAYTYPE(430,"no_cash","非现金支付类型"),
	ORDER_STATE_UNKNOWN(115,"0051","未知的订单状态"),
	ORDER_STATE_FAIL(116,"8202","未支付"),
	REC_SINGLE_NO(0,"单边账标记(不存在单边账)"),
	CASH_PAYTYPE(43,"0049","现金"),
	PAY_TYPE_WECHAT(45,"0249","微信"),
	PAY_TYPE_ALIPAY(46,"0349","支付宝"),//支付类型
	PAY_TYPE_AGGREGATE(47,"1649","聚合支付"),//支付类型
	PAY_TYPE_WJYZT(48,"2649","武进区一账通"),//支付类型
	PAY_TYPE_UNIONPAY(49,"3649","云闪付"),//支付类型
	PAY_TYPE_GZBANK(44,"gzbank","贵州银行卡"),//兴义医院支付类型
	PAY_CHANNEL_WECHAT(78,"weixin","微信"),
	PAY_CHANNEL_ALIPAY(79,"zhifubao","支付宝"),//支付渠道
	PAY_PAY_SOURCE(49,"Pay_Source","账单来源"),//账单来源
	PAY_TYPE_BANK(44,"0149","银行卡"),
	PAY_TYPE_SBK(44,"sbk","社保卡银行卡"),
	PAY_TYPE_HEALTHCARE(49,"0449","医保"),
	PAY_TYPE_ONLINE_BANK(49,"0549","网银"),
	ORG_NO_TEMP(21,"5307601","机构编码值"),
	ORG_NO_TEMP_TWO(27,"5307607","机构编码值"),
	ORDER_STATE_SUCCUSS(117,"成功"),
	PAY_BUSINESS_REGISTER(58,"0451","成功"),
	PAT_TYPE_MZ(172,"mz","门诊"),
	PAT_TYPE_ZY(173,"zy","住院"),
	PAT_TYPE_QT(173,"qt","其他"),
	PAT_TYPE_ZYMZ(191,"zymz","总计"),
	REC_TYPE_TWO(191,"two_rec","两方对账"),
	REC_TYPE_THREE(191,"three_rec","三方对账"),
	BILL_SOURCE_SELF(0,"self",""),
	BILL_SOURCE_SELF_JD(0,"self_jd","巨鼎"),
	BILL_SOURCE_SELF_TD_JD(0,"self_td_jd","唐都金蝶"),
	BILL_SOURCE_SELF_WN_JD(0,"self_wn_jd","渭南金蝶"),
	BILL_SOURCE_THIRD(0,"third","第三方"),
	BILL_SOURCE_ICBC(0,"icbc","工行"),
	BILL_SOURCE_ICBC_JHZF(0, "icbcjhzf", "工行聚合支付"),
	BILL_SOURCE_ICBC_YHK(0, "icbcyhk", "工行银行卡"),
	BILL_SOURCE_DYT(0,"dyt","滇医通"),
	BILL_SOURCE_HISCK(0,"his_ck","HIS窗口"),
	BILL_SOURCE_ZSYY(0,"zsyy","掌上医院"),
	BILL_SOURCE_BOC(0,"boc","中国银行"),
	BILL_SOURCE_CCB(0,"ccb","中国建设银行"),
	BILL_SOURCE_CIB(0,"cib","兴业银行"),
	BILL_SOURCE_YSH(0,"ysh","银商行"),
	BILL_SOURCE_ZZJ(0, "zzj", "自助机"),
	BILL_SOURCE_SELF_VERSION_TWO(0, "self_version_two", "支付服务2.0_智慧服务平台"),
	TRADE_TYPE_PAY(0,"0156","缴费"),
	TRADE_TYPE_REFUND(0,"0256","退费"),
	SETTLEMENT_TYPE(0,"0031","自费"),
	BILL_SOURCE_DMF(0,"dmf","当面付支付"),
	BILL_SOURCE_BA(0,"ba","病案"),
	BILL_SOURCE_MISPOS(0, "mispos", "MISPOS"),
	BILL_SOURCE_HIS_WINDOW(0,"0830","HIS窗口"),
	ADVANCE_PAY_SUMMARY_PAY(0,"0","预收款汇总_预约支付"),
	ADVANCE_PAY_SUMMARY_USED(0,"1","预收款汇总_预约已就诊"),
	ADVANCE_PAY_SUMMARY_REFUND(0,"1","预收款汇总_预约已退费"),
	BILL_SOURCE_ZRYY_MIS(0,"zryy_mis","中日友好mis"),
	BILL_SOURCE_YSF(0,"ysf","云闪付"),

	// 内蒙妇幼
	BILL_SOURCE_APP(0,"app","app"),
	// ---> end

	// ---> 焦作定制
	PAY_TYPE_OC(45, "3749", "预交金"),
	BILL_SOURCE_ZJ_APP(0, "zj_wx", "微信公众号"),
	BILL_SOURCE_ZJ_ZZJ(0, "his_zz", "自助机"),
	BILL_SOURCE_ZJ_ZJP(0, "ZJP", "诊间屏"),
	BILL_SOURCE_ZJ_WZ(0, "WZ", "未知来源"),
	// ---> 焦作定制 end

	//兴义
	BILL_SOURCE_BAZZ(0,"bazz","病案自助"),
	BILL_SOURCE_DJYY(0,"djyy","担架预约"),

	//广水
	BILL_SOURCE_GSZZJ(0, "gs_zzj", "自助机"),

	//昆明延安窗口
	BILL_SOURCE_KMYA_CK(0, "kmya_ck", "窗口"),

	BILL_SOURCE_YX_ZZJ(0, "zzj", "自助机"),

	//孙逸仙
	BILL_SOURCE_ZHJHZF(0, "zhjhzf", "中行聚合支付"),
	BILL_SOURCE_YB(0, "yb", "医保"),

	//漯河市中心医院
	BILL_SOURCE_ZYZZJ(0,"zyzzj","总院自助机"),
	BILL_SOURCE_ZYMZCK(0,"zymzck","总院门诊窗口"),
	BILL_SOURCE_ZYZYCK(0,"zyzyck","总院住院窗口"),
	BILL_SOURCE_WM(0,"wm","微脉"),
	BILL_SOURCE_ZYQLC(0,"zyqlc","总院全流程"),
	BILL_SOURCE_ZYDZJKK(0,"zydzjkk","总院电子健康卡"),
	BILL_SOURCE_ZYYB(0,"zyyb","总院医保"),
	BILL_SOURCE_ZYXNH(0,"zyxnh","总院新农合"),
	BILL_SOURCE_XCMZ(0,"xcmz","西城分院门诊"),
	BILL_SOURCE_XCZY(0,"xczy","西城分院住院"),
	BILL_SOURCE_XCYB(0,"xcyb","西城分院医保"),
	BILL_SOURCE_YMZ(0,"ymz","一分院门诊"),
	BILL_SOURCE_YZY(0,"yzy","一分院住院"),
	BILL_SOURCE_YYB(0,"yyb","一分院医保"),


	//深圳大学总医院
	BILL_SOURCE_JY160(0,"JY160","就医160"),
	BILL_SOURCE_PAHYS(0,"PAHYS","平安好医生"),
	BILL_SOURCE_JKSZ(0,"JKSZ","健康深圳"),
	BILL_SOURCE_YZT(0,"YZT","一账通"),


	PAY_TYPE_OTHER(0,"9949","其他")
	;

	private Integer id;
	private String value;
	private String code;
	private EnumTypeOfInt(Integer id,String value,String code) {
		this.id = id;
		this.value = value;
		this.code=code;
	}
	private EnumTypeOfInt(Integer id,String value) {
		this.id = id;
		this.value = value;
	}
	public String getValue() {
		return value;
	}

    /**
     * 通过code获取value
     *
     * @param code code值
     * @return value值
     */
    public static String getValueByCode(String code) {
        return Arrays.stream(EnumTypeOfInt.values())
                .filter(enumTypeOfInt -> enumTypeOfInt.getCode() != null)
                .filter(enumTypeOfInt -> enumTypeOfInt.getCode().equals(code))
                .findAny().orElse(EnumTypeOfInt.HANDLE_FAIL).getValue();
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



}
