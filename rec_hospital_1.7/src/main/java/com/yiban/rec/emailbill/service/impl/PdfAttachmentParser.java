package com.yiban.rec.emailbill.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.emailbill.service.AttachmentParser;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;

/**
 * @author swing
 * @date 2018年6月25日 下午3:57:38 类说明
 */
public class PdfAttachmentParser implements AttachmentParser {
	
	private File file;
	

	public PdfAttachmentParser(File file) {
		this.file = file;
	}


	public String parseFileToString() throws Exception {
		PDDocument document = PDDocument.load(file);
		// 获取页码
		int pages = document.getNumberOfPages();
		// 读文本内容
		PDFTextStripper stripper = new PDFTextStripper();
		// 设置按顺序输出
		stripper.setSortByPosition(true);
		stripper.setStartPage(1);
		stripper.setEndPage(pages);
		String content = stripper.getText(document);
		document.close();
		return content;
	}


	@Override
	public List<ThirdBill> convertToBean() {
		String bankBillTerminal = PropertyUtil.getProperty("activemq.bank.bill.terminal", "11602014,11602015,11602016,11602017,11602018,11602019,11602020,11602021,11602022,11602023");
		String[] bankBillTerminalArray = bankBillTerminal.split(",");
		Map<String, Object> map = new HashMap<String, Object>(50);
		for(String str : bankBillTerminalArray) {
			map.put(str, str);
		}
		List<ThirdBill> list = new ArrayList<ThirdBill>(50);
		String parseResult=null;
		try{
			 parseResult=this.parseFileToString();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(parseResult !=null && parseResult.length() >0){
			String fileName=file.getName();
			String[] nameArr=fileName.split("_");
			String orgCode=nameArr[0];
			String[] lines = parseResult.split("\n");
			for (int i = 0; i < lines.length; i++) {
				if (i >= 4) {
					String ln = lines[i];
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
					tb.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
					tb.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
					tb.setOrgNo(orgCode);
					tb.setPaySource("9948");
					tb.setPayType("0149");
					tb.setRecPayType("0149");
					tb.setPayFlowNo(payFlowNo);
					tb.setOrderState("0156");
					tb.setShopFlowNo(payFlowNo);
					tb.setPayAmount(new BigDecimal(payAmount));
					tb.setTradeDatatime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", tradeDateTime));
					list.add(tb);
				}
				
			}
		}
		return list;
	}

}
