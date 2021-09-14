package com.yiban.rec.web.admin.refundButton;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.rec.bill.parse.util.ProConfigManager;

@Controller
public class RefundButtonController {
	
	@Autowired
	private EntityManager entityManager;
	
	@GetMapping(value="electronic/refund/Button")
	@ResponseBody
	public String getRefundButton() {
		String button=ProConfigManager.getValueByPkey(entityManager,"refund.button");
		return button;
	} 
	
}
