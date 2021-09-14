package com.yiban.rec.bill.parse.service.standardbill.impl.bank;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;
import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.service.changefileformat.FileParserable;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractEmailBilParser;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.domain.ThirdBill;

/**
 * @author swing
 * @date 2018年7月26日 上午10:54:44 类说明 中国银行邮件账单解析
 */
public abstract class BankEmailBillParser extends AbstractEmailBilParser {

	/**
	 * 解析pdf文件解析
	 */
	@Override
	protected List<ThirdBill> parseFile(File file,String orgNo) {
		logger.info("中国银行邮件账单解析");
		List<ThirdBill> list = new ArrayList<ThirdBill>(50);
		if(file == null){
			return list;
		}
		FileParserable parser = getFileParserable();
		List<String> pdfContentsList = parser.fileToList(file);
		String bankBillTerminal = PropertyUtil.getProperty("activemq.bank.bill.terminal",
				"11602014,11602015,11602016,11602017,11602018,11602019,11602020,11602021,11602022,11602023");
		String[] bankBillTerminalArray = bankBillTerminal.split(",");
		Map<String, Object> map = new HashMap<String, Object>(50);
		for (String str : bankBillTerminalArray) {
			map.put(str, str);
		}

		if (pdfContentsList.size() > 0) {
			String fileName = file.getName();
			String[] nameArr = fileName.split("_");
			String orgCode = nameArr[0];

			for (int i = 0; i < pdfContentsList.size(); i++) {
				if (i >= 4) {
					String ln = pdfContentsList.get(i);
					String[] coluns = ln.split(" ");
					if (coluns.length < 5) {
						continue;
					}
					ThirdBill tb = new ThirdBill();
					String terminalNum = coluns[0];
					if (!map.containsKey(terminalNum)) {
						continue;
					}
					String tradeDateTime = coluns[1] + " " + coluns[2];
					String payFlowNo = null;
					String payAmount = coluns[5].replace(",", "");
					if (coluns.length == 10) {
						payFlowNo = coluns[9];
					} else if (coluns.length == 11) {
						payFlowNo = coluns[10];
					}
					payFlowNo = payFlowNo.replaceAll("\\r", "");
					tb.setCreatedDate(new Date());
					tb.setIsActived(ActiveEnum.YES.getValue());
					tb.setIsDeleted(DeleteEnum.NO.getValue());
					tb.setOrgNo(orgCode);
					tb.setPaySource("9948");
					tb.setPayType(EnumTypeOfInt.PAY_TYPE_BANK.getValue());
					tb.setRecPayType(EnumTypeOfInt.PAY_TYPE_BANK.getValue());
					tb.setPayFlowNo(payFlowNo);
					tb.setOrderState(EnumTypeOfInt.TRADE_TYPE_PAY.getValue());
					tb.setShopFlowNo(payFlowNo);
					tb.setPayAmount(new BigDecimal(payAmount));
					tb.setTradeDatatime(DateUtils.transferStringToDate("yyyy-MM-dd HH:mm:ss", tradeDateTime));
					list.add(tb);
				}

			}
		}
		return list;
	}
	
	public abstract FileParserable getFileParserable();
}
