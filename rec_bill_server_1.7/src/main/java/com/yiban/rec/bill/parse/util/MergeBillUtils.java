package com.yiban.rec.bill.parse.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.util.RandomCodeUtil;

public class MergeBillUtils {

	/**
	 * 合并his账单，根据(流水号+状态)
	 * 
	 * @param hisList
	 * @param mergeRefund true:合并所有账单，false:不合并退费账单
	 * @return
	 */
	public static List<HisTransactionFlow> mergeHis(List<HisTransactionFlow> hisList, boolean mergeRefund) {
		if (hisList.size() == 0) {
			return hisList;
		}

		HashMap<String, HisTransactionFlow> map = new HashMap<>();
		String key;
		HisTransactionFlow his = null;
		for (int i = 0, len = hisList.size(); i < len; i++) {
			his = hisList.get(i);
			String payFlowNo = his.getPayFlowNo();
			String state = his.getOrderState();

			// 不合并退费状态的账单，将state设置为时间戳，避免重复
			if (!mergeRefund && EnumTypeOfInt.REFUND_CODE.getValue().equals(state)) {
				state = System.currentTimeMillis() + RandomCodeUtil.generateWord(3);
			}
			if (StringUtils.isEmpty(payFlowNo)) {
				payFlowNo = System.currentTimeMillis() + RandomCodeUtil.generateWord(3);
			}
			key = payFlowNo + state;
			if (map.containsKey(key)) {
				HisTransactionFlow hisTemp = map.get(key);
				BigDecimal amount = his.getPayAmount();
				hisTemp.setPayAmount(amount.add(hisTemp.getPayAmount()));
				map.put(key, hisTemp);
			} else {
				map.put(key, his);
			}
		}
		return new ArrayList<HisTransactionFlow>(map.values());
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			boolean mergeRefund=false;
			String state="0256";
			// 不合并退费状态的账单，将state设置为时间戳，避免重复
			if (!mergeRefund && EnumTypeOfInt.REFUND_CODE.getValue().equals(state)) {
				state = System.currentTimeMillis() + "."+RandomCodeUtil.generateWord(3);
			}
			System.out.println(state);
		}
		
	}
}
