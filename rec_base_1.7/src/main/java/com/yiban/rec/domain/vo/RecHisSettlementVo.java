package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * HIS结算账单明细
 * @Author WY
 * @Date 2019年1月10日
 */
public class RecHisSettlementVo {
    /** 通信状态码 */
    private String resultCode;
    
    /** 通信提示 */
    private String resultMsg;
    
    /** 返回数据 */
    private List<RecHisSettlementDetails> data;
    
    public RecHisSettlementVo() {
        super();
    }

    public RecHisSettlementVo(String resultCode, String resultMsg, 
            List<RecHisSettlementDetails> data) {
        super();
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public List<RecHisSettlementDetails> getData() {
        return data;
    }

    public void setData(List<RecHisSettlementDetails> data) {
        this.data = data;
    }

    /**
     * 结算明细
     * @Author WY
     * @Date 2019年1月11日
     */
   public class RecHisSettlementDetails {
        /** HIS流水号 */
        private String hisOrderNo;
        
        /** 病人ID */
        private String patientId;
        
        /** 金额（2位小数）退费为负数  缴费为正数 */
        private BigDecimal amount;
        
        /** 支付类型  微信（0249）或者支付宝（0349） */
        private String payType;
        
        /** 交易类型  缴费（0156）或者退费（0256） */
        private String orderType;
        
        /** 交易时间（yyyy-MM-dd HH:mm:ss） */
        private String payTime;
        
        /** 结账时间（yyyy-MM-dd HH:mm:ss） */
        private String settlementTime;
        
        /** 结账日期（yyyy-MM-dd） */
        private String settlementDate;
        
        /** 结算人编号 */
        private String settlementorNum;
        
        /** 第三方业务系统流水号（微信支付宝、银行等支付成功返回的订单号） */
        private String tnsOrderNo;
        
        /** 账单来源  金蝶（self_td_jd）巨鼎（self）*/
        private String billSource;
        
        /** 结算批次号 */
        private String settlementNumber;
        
        /** 机构编码 */
        private String orgCode;
        
        // 患者姓名
    	private String patientName;
    	
    	// 结账序号
    	private String settlementSerialNo;
    	
    	// 商户流水号
    	private String outTradeNo;
        
    	//支付业务类型
    	private String payBusinessType;

        public String getHisOrderNo() {
            return hisOrderNo;
        }

        public void setHisOrderNo(String hisOrderNo) {
            this.hisOrderNo = hisOrderNo;
        }

        public String getPatientId() {
            return patientId;
        }

        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getPayTime() {
            return payTime;
        }

        public void setPayTime(String payTime) {
            this.payTime = payTime;
        }

        public String getSettlementTime() {
            return settlementTime;
        }

        public void setSettlementTime(String settlementTime) {
            this.settlementTime = settlementTime;
        }

        public String getSettlementDate() {
            return settlementDate;
        }

        public void setSettlementDate(String settlementDate) {
            this.settlementDate = settlementDate;
        }

        public String getSettlementorNum() {
            return settlementorNum;
        }

        public void setSettlementorNum(String settlementorNum) {
            this.settlementorNum = settlementorNum;
        }

        public String getTnsOrderNo() {
            return tnsOrderNo;
        }

        public void setTnsOrderNo(String tnsOrderNo) {
            this.tnsOrderNo = tnsOrderNo;
        }

        public String getBillSource() {
            return billSource;
        }

        public void setBillSource(String billSource) {
            this.billSource = billSource;
        }

        public String getSettlementNumber() {
            return settlementNumber;
        }

        public void setSettlementNumber(String settlementNumber) {
            this.settlementNumber = settlementNumber;
        }

        public String getOrgCode() {
            return orgCode;
        }

        public void setOrgCode(String orgCode) {
            this.orgCode = orgCode;
        }

		public String getPatientName() {
			return patientName;
		}

		public void setPatientName(String patientName) {
			this.patientName = patientName;
		}

		public String getSettlementSerialNo() {
			return settlementSerialNo;
		}

		public void setSettlementSerialNo(String settlementSerialNo) {
			this.settlementSerialNo = settlementSerialNo;
		}

		public String getOutTradeNo() {
			return outTradeNo;
		}

		public void setOutTradeNo(String outTradeNo) {
			this.outTradeNo = outTradeNo;
		}

		public String getPayBusinessType() {
			return payBusinessType;
		}

		public void setPayBusinessType(String payBusinessType) {
			this.payBusinessType = payBusinessType;
		}
    }
}
