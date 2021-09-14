package com.yiban.rec.bill.parse.service.standardbill.impl.bank;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractDataSourceBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;

/**
 * 银行数据源账单解析
 * 
 * 1.数据源获取账单；2.入库
 * 
 * @author clearofchina
 *
 */
public class BankDataSourceBillParser extends AbstractDataSourceBillParser<ThirdBill> {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 解析账单返回账单列表
	 */
	@Override
	public List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		
		String sql = "SELECT '"+orgCode+"' as orgCode,  txn_amt AS txnAmt, tran_date AS tranDate,rrn AS rrn  FROM HIST_TRANS ht"
				+ " WHERE to_char(ht.tran_date, 'yyyy-mm-dd') =? AND trim(ht.resp_detail)='交易成功'";
		log.info("获取银行sql:" + sql);
		
		String jdbcUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.misPosJdbcUrl);
		String driverClass = ProConfigManager.getValueByPkey(entityManager, ProConstants.misPosDriverClass);
		String username = ProConfigManager.getValueByPkey(entityManager, ProConstants.misPosUsername);
		String password = ProConfigManager.getValueByPkey(entityManager, ProConstants.misPosPassword);
		
		List<ThirdBill> bills = query(jdbcUrl, driverClass, username, password, sql, new ThirdBillRowMapper(), date);
		return bills;
	}

}

class ThirdBillRowMapper implements RowMapper<ThirdBill> {
	@Override
	public ThirdBill mapRow(ResultSet rs, int rowNum) throws SQLException {
		ThirdBill thirdBill = new ThirdBill();
		thirdBill.setOrgNo(rs.getString("orgCode"));
		thirdBill.setCreatedDate(new Date());
		thirdBill.setIsActived(ActiveEnum.YES.getValue());
		thirdBill.setIsDeleted(DeleteEnum.NO.getValue());
		// 支付类型：0149 银行卡支付
		thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_BANK.getValue());
		thirdBill.setRecPayType(EnumTypeOfInt.PAY_TYPE_BANK.getValue());
		thirdBill.setPayFlowNo(rs.getString("rrn"));
		thirdBill.setPayAmount(new BigDecimal(rs.getDouble("txnAmt")));
		thirdBill.setTradeDatatime(DateUtils.transferStringToDate("yyyy-MM-dd HH:mm:ss", rs.getString("tranDate")));
		thirdBill.setOrderState(EnumTypeOfInt.PAY_CODE.getValue());
		return thirdBill;
	}

}
