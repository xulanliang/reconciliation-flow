package com.yiban.rec.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.service.DrmService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.StringUtil;

@Service
public class DrmServiceImpl extends BaseOprService implements DrmService{
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private GatherService gatherService;

	@Override
	public Page<Map<String, Object>> getDrmPageList(PageRequest pagerequest, String bankTypeId,String dataTime) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT t.org_no as orgNo,DATE_FORMAT(t.Trade_datatime,'%Y-%m-%d') AS tradeDatatime,t.device_no AS deviceNo,(SELECT d.device_area FROM t_device d WHERE "
				+ "t.device_no=d.device_no) AS deviceArea,t.Pay_Shop_No AS payShopNo,s.meta_data_bank_id as metaDataBankId,"
				+ "SUM(t.Pay_Amount) AS payAmount,COUNT(*) AS paySum FROM t_rec_platformflow_log t "
				+ "LEFT JOIN t_shop_info s ON t.Pay_Shop_No=s.pay_shop_no WHERE t.Pay_Type="+EnumType.PAY_CODE_CASH.getValue()+" and t.is_deleted=0 AND t.is_actived=1");
		if (!StringUtil.isEmpty(bankTypeId))
			sb.append(" AND s.meta_data_bank_id ='").append(bankTypeId).append("'");
		if(!StringUtil.isNullOrEmpty(dataTime)){
			sb.append(" and t.trade_datatime >= '").append(dataTime).append("'");
			sb.append(" and t.trade_datatime <= '").append(dataTime+" 23:59:59").append("'");
		}
		sb.append(" group by t.device_no");
		Page<Map<String, Object>> page = super.handleNativeSql(sb.toString(), pagerequest,
				new String[] { "orgNo","tradeDatatime","deviceNo","deviceArea", "payShopNo", "metaDataBankId", "payAmount", "paySum" });
		return page;
	}
	
	@Override
	public List<Map<String, Object>> getDrmList(String bankTypeId,String dataTime) {
		StringBuffer sb = new StringBuffer();
		Map<String,String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String,Object> orgMap = gatherService.getOrgMap();
		sb.append("SELECT t.org_no as orgNo,DATE_FORMAT(t.Trade_datatime,'%Y-%m-%d') AS tradeDatatime,t.device_no AS deviceNo,(SELECT d.device_area FROM t_device d WHERE "
				+ "t.device_no=d.device_no) AS deviceArea,t.Pay_Shop_No AS payShopNo,s.meta_data_bank_id as metaDataBankId,"
				+ "SUM(t.Pay_Amount) AS payAmount,COUNT(*) AS paySum FROM t_rec_platformflow_log t "
				+ "LEFT JOIN t_shop_info s ON t.Pay_Shop_No=s.pay_shop_no WHERE t.Pay_Type="+EnumType.PAY_CODE_CASH.getValue()+" and t.is_deleted=0 AND t.is_actived=1");
		if (!StringUtil.isEmpty(bankTypeId))
			sb.append(" AND s.meta_data_bank_id ='").append(bankTypeId).append("'");
		if(!StringUtil.isNullOrEmpty(dataTime)){
			sb.append(" and t.trade_datatime >= '").append(dataTime).append("'");
			sb.append(" and t.trade_datatime <= '").append(dataTime+" 23:59:59").append("'");
		}
		sb.append(" group by t.device_no");
		List<Map<String, Object>> list = super.handleNativeSql(sb.toString(),
				new String[] { "orgNo","tradeDatatime","deviceNo","deviceArea", "payShopNo", "metaDataBankId", "payAmount", "paySum" });
		if(!StringUtil.isNullOrEmpty(list)){
			for(Map<String,Object> map : list){
				map.put("orgName", orgMap.get(map.get("orgNo").toString()));
				map.put("metaDataBankName", metaMap.get(map.get("metaDataBankId").toString()));
			}
		}
		return list;
	}

}
