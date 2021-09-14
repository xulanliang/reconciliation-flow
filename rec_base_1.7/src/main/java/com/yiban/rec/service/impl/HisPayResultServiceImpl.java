package com.yiban.rec.service.impl;
import org.springframework.stereotype.Service;

import com.yiban.rec.service.HisPayResultService;
@Service("hisPayResultService")
public class HisPayResultServiceImpl implements HisPayResultService {
	/*private final String PAY_HOST = Configure.getPropertyBykey("pay.center.url");
	@Autowired
	private OrganizationDao organizationDao;
	
	@Override
	public String upPaymentDescend(String orgCode, String startTime, String endTime) {
		try {
			String url=PAY_HOST+"/pay/order/orderList";
			String retStr = "";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("orgCode", orgCode);
			map.put("startTime", startTime);
			map.put("endTime", endTime);
			//得到支付接口的订单数据
			retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
			JSONArray jsonArray = JSONArray.fromObject(retStr);
		} catch (Exception e) {
			e.printStackTrace();
			return "err";
		}
		return "success";
	}
	public Long getOrgIdMap(String code) {
		Organization vo = organizationDao.findByCode(code);
		if(vo!=null) {
			return vo.getId();
		}
		return null;
	}*/

}
