package com.yiban.rec.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.util.YiBanEntityUtils;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.PayType;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.EnumTypeOfInt;

@Controller
@RequestMapping("/admin/reconciliation")
public class UserOrganizationPerController extends CurrentUserContoller{
	
	@Autowired
	private UserOrganizationPerService userOrganizationPerService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	
	@RequestMapping(value = "/organizationsList_nopage.json", method = RequestMethod.GET)
	@ResponseBody
    public List<Organization> organizationlist(boolean includeAll){
		User user = currentUser();
		List<Organization> orgs = userOrganizationPerService.orgTempList(user);
		if(includeAll){
			Organization org = new Organization();
			org.setCode("全部");
			org.setName("全部");
			orgs.add(0, org);
		}
		YiBanEntityUtils.filterChild(orgs);
        return orgs;
    }
	@RequestMapping("/typeValue")
	@ResponseBody
	public List<MetaData> getMetaDatas(String typeValue, boolean isIncludeAll){
		
		List<MetaData> list = metaDataService.findMetaDataByDataTypeValue(typeValue);
		if(typeValue.toLowerCase().equals("trade_code")){
			for(int i=list.size()-1;i>=0;i--){
				MetaData metaData = list.get(i);
				if(metaData.getValue().equals(EnumTypeOfInt.TRADE_CODE_PAY.getValue())
						||metaData.getValue().equals(EnumTypeOfInt.TRADE_CODE_REFUND.getValue())
						||metaData.getValue().equals(EnumTypeOfInt.TRADE_CODE_REVERSAL.getValue())||metaData.getValue().equals(EnumTypeOfInt.TRADE_CODE_REVOKE.getValue())){
				}else{
					list.remove(i);
				}
			}
		}
		/*if(typeValue.toLowerCase().equals("pay_type")){
			for(int i=list.size()-1;i>=0;i--){
				MetaData metaData = list.get(i);
				if(metaData.getValue().equals(EnumTypeOfInt.CASH_PAYTYPE.getValue())){
					list.remove(i);
				}
			}
		}*/
		if(isIncludeAll){
			MetaData metaData = new MetaData();
			metaData.setId(CommonConstant.ALL_ID);
			metaData.setName("全部");
			metaData.setValue("全部");
			list.add(0, metaData);
		}
		return list;
	}
	
	@RequestMapping("/payType")
	@ResponseBody
	public List<PayType> getPayTypeDatas(String typeValue){
		List<PayType> list = null;
		if("weixin".equals(typeValue)){
			list = userOrganizationPerService.getPayTypeByType(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
		}else if("zhifubao".equals(typeValue)){
			list = userOrganizationPerService.getPayTypeByType(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
		}else if("payTypeAll".equals(typeValue)){
			String[] allTypes = new String[2];
			allTypes[0] = EnumTypeOfInt.PAY_TYPE_WECHAT.getValue();
			allTypes[1] = EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue();
			list = userOrganizationPerService.getPayTypeByTypes(allTypes);
		}else if("Pay_Source".equals(typeValue)){
			list = userOrganizationPerService.getPayTypeByType(EnumTypeOfInt.PAY_PAY_SOURCE.getValue());
		}
		return list;
	}
}
