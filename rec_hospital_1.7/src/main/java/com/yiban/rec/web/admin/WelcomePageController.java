package com.yiban.rec.web.admin;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.service.UserRoleService;
import com.yiban.rec.service.WelcomePageService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;


@RestController
@RequestMapping({"/admin/welcome/startPage/data"})
public class WelcomePageController extends CurrentUserContoller {
	
	@Autowired
	private WelcomePageService welcomePageService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private UserRoleService userRoleService;
	/**
	 * 电子对账
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/electronInfo")
	public ResponseResult electronInfo(String orgCode){
		List<Map<String, Object>> data = welcomePageService.electronInfo(orgCode);
		DecimalFormat df = new DecimalFormat("#,##0.00") ; 
		df.setRoundingMode(RoundingMode.HALF_UP);
		for(Map<String, Object> map : data){
			map.put("thridAmount", df.format(map.get("thridAmount")));
			map.put("hisAmount", df.format(map.get("hisAmount")));
			map.put("diffAmount", df.format(map.get("diffAmount")));
		}
		return ResponseResult.success().data(data);
	}
	/**
	 * 不同渠道折线图
	 * @param orgCode
	 * @param billSource
	 * @return
	 */
	@RequestMapping("/billSourceLine")
	public ResponseResult billSourceLine(String orgCode,String billSource){
		List<Map<String, Object>> list = welcomePageService.billSourceLine(orgCode,billSource);
		List<MetaData> billSources = metaDataService.findByTypeIdOrderBySort("bill_source");
//		Collections.sort(billSources,new BeanComparator<>("sort"));
		List<String> legends = new ArrayList<>();
		for(MetaData metaData:billSources){
			legends.add(metaData.getValue());
		}
		Map<String, Object> data = dataToEchatrs(list,legends,"billSource","date","amount");
		return ResponseResult.success().data(data);
	}
	/**
	 * 近七天业务收入汇总
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/businessIncomeChart")
	public ResponseResult businessIncomeChart(String orgCode){
		List<String> legends= new ArrayList<>(Arrays.asList("0151","0451","0551"));
		List<Map<String, Object>> list = welcomePageService.businessIncomeChart(orgCode);
		if(list==null||list.size()==0){
			Map<String, Object> data = dataToEchatrs(list,"businessType","date","amount");
			return ResponseResult.success().data(data);
		}
		Map<String, Object> data = dataToEchatrs(list,legends,"businessType","date","amount");
		return ResponseResult.success().data(data);
	}
	/**
	 * 近3月渠道收入汇总
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/initBillsRelateThreeMonthsIncomeData")
	public ResponseResult initBillsRelateThreeMonthsIncomeData(String orgCode){
		/*List<String> legends= new ArrayList<>(Arrays.asList("一月","二月","三月"));*/
		List<String> legends= new ArrayList<>();
		Date date = new Date();
		for (int i = 3; i >= 1; i--){
			legends.add(welcomePageService.getMonthName(DateUtil.getSpecifiedDayBeforeMonth(date, i)));
		}
		List<Map<String, Object>> list = welcomePageService.initBillsRelateThreeMonthsIncomeData(orgCode);
		/*if(list==null||list.size()==0){
			Map<String, Object> data = dataToEchatrs(list,"businessType","date","amount");
			return ResponseResult.success().data(data);
		}*/
		Map<String, Object> data = billDataToEchatrs(list,legends,"date", "billSource", "amount");
		return ResponseResult.success().data(data);
	}
	/**
	 * 近七天业务收入汇总
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/businessIncomeSummary")
	public ResponseResult businessIncomeSummary(String orgCode){
		DecimalFormat df = new DecimalFormat("#,##0.00") ; 
		List<String> legends= new ArrayList<>(Arrays.asList("0151","0451","0551"));
		List<Map<String, Object>> list = welcomePageService.businessIncomeSummary(orgCode);
		for (Map<String, Object> map : list) {
			map.put("amount", df.format(map.get("amount")));
		}
		boolean flag = false;
		Map<String, Object> _map = null;
		for(String l:legends){
			flag=false;
			for (Map<String, Object> map : list) {
				if(l.equals(map.get("businessType"))){
					flag=true;
					break;
				}
			}
			if(!flag){
				_map=new HashMap<>();
				_map.put("businessType", l);
				_map.put("amount", "0.00");
				list.add(_map);
			}
		}
		return ResponseResult.success().data(list);
	}
	/**
	 * 订单信息
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/orderInfo")
	public ResponseResult orderInfo(String orgCode){
		User user = currentUser();
		String type=null;
		List<Object> list = userRoleService.getRoleName(String.valueOf(user.getId()));
		if(list.contains("医院管理员")||list.contains("财务主任")||"admin".equals(user.getLoginName())) {
			type="1";
		}else {
			type="2";
		}
		String warningCount = welcomePageService.getWarningCount(orgCode);
		Map<String,Object> refundCount = welcomePageService.getRefundCount(orgCode,type,user);
		Map<String, Object> map = new HashMap<>();
		map.put("warningCount", warningCount);
		map.put("refundCount", refundCount.get("1"));//'状态  0:无意义  1：待审核，2已驳回，3已退费'
		map.put("refundTurnCount", refundCount.get("2"));
		return ResponseResult.success().data(map);
	}
	/**
	 * 渠道名称分类占比
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/thridPie")
	public ResponseResult thridPie(String orgCode){
		List<Map<String, Object>> list = welcomePageService.thridPie(orgCode);
		Map<String, Object> data = new HashMap<>();
		List<MetaData> billSources = metaDataService.findByTypeIdOrderBySort("bill_source");
		List<String> legends = new ArrayList<>();
		boolean flag = false;
		String value = "";
		String name = "";
		Map<String, Object> billMap = null;
		for(MetaData metaData:billSources){
			value = metaData.getValue();
			name = metaData.getName();
			legends.add(name);
			flag=false;
			for(Map<String, Object> map:list){
				if(value.equals(map.get("name"))){
					flag=true;
					map.put("name", name);
					break;
				}
			}
			if(!flag){
				billMap=new HashMap<>();
				billMap.put("name", name);
				billMap.put("value", "0.00");
				list.add(billMap);
			}
		}
		data.put("data", list);
		data.put("legend", legends);
		return ResponseResult.success().data(data);
	}
	/**
	 * 支付类型分类占比
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/payTypePie")
	public ResponseResult payTypePie(String orgCode){
		List<Map<String, Object>> list = welcomePageService.payTypePie(orgCode);
		Map<String, Object> data = new HashMap<>();
		List<MetaData> billSources = metaDataService.findByTypeIdOrderBySort("Pay_Type");
		List<String> legends = new ArrayList<>();
		boolean flag = false;
		String value = "";
		String name = "";
		Map<String, Object> billMap = null;
		for(MetaData metaData:billSources){
			value = metaData.getValue();
			name = metaData.getName();
			legends.add(name);
			flag=false;
			for(Map<String, Object> map:list){
				if(value.equals(map.get("name"))){
					flag=true;
					map.put("name", name);
					break;
				}
			}
//			if(!flag){
//				billMap=new HashMap<>();
//				billMap.put("name", name);
//				billMap.put("value", "0.00");
//				list.add(billMap);
//			}
		}
		data.put("data", list);
		data.put("legend", legends);
		return ResponseResult.success().data(data);
	}
	/**
	 * 近七天收入分布情况
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/payTypeIncomeChart")
	public ResponseResult payTypeIncomeChart(String orgCode){
		List<Map<String, Object>> list = welcomePageService.payTypeIncomeChart(orgCode);
		Map<String, Object> data = dataToEchatrs(list,"payType","date","amount");
		return ResponseResult.success().data(data);
	}
	/**
	 * 现金对账
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/cashInfo")
	public ResponseResult cashInfo(String orgCode){
		DecimalFormat df = new DecimalFormat("#,##0.00") ; 
		List<Map<String, Object>> list = welcomePageService.cashInfo(orgCode);
		for (Map<String, Object> map : list) {
			map.put("cashAmount", df.format(map.get("cashAmount")));
			map.put("hisAmount", df.format(map.get("hisAmount")));
			map.put("diffAmount", df.format(map.get("diffAmount")));
		}
		return ResponseResult.success().data(list);
	}
	/**
	 * 医保对账
	 * @param orgCode
	 * @return
	 */
	@RequestMapping("/healthcareInfo")
	public ResponseResult healthcareInfo(String orgCode){
		DecimalFormat df = new DecimalFormat("#,##0.00") ; 
		Map<String, Object> map = welcomePageService.healthcareInfo(orgCode);
		map.put("hisAmount", df.format(map.get("hisAmount")));
		map.put("healthcareAmount", df.format(map.get("healthcareAmount")));
		map.put("diffAmount", df.format(map.get("diffAmount")));
		return ResponseResult.success().data(map);
	}
	/**
	 * @param list
	 * @param key
	 * @return
	 */
	private List<String> getLegend(List<Map<String, Object>> list,String key){
		List<String> legends = new ArrayList<>();
		for(Map<String, Object> map : list){
			if(!legends.contains(map.get(key).toString())){
				legends.add(map.get(key).toString());
			}
		}
		return legends;
	}
	/**
	 * 获取X坐标
	 * @return
	 */
	private List<String> getXAxis(){
		List<String> xAxis = new ArrayList<>();
		String recDate = welcomePageService.getRecDate();
		Date date = DateUtil.transferStringToDate("yyyy-MM-dd", recDate);
		for(int i=6;i>0;i--){
			xAxis.add(DateUtil.transferDateToDateFormat("yyyy-MM-dd", DateUtil.addDay(date, -i)));
		}
		xAxis.add(recDate);
		return xAxis;
	}
	private Map<String, Object> dataToEchatrs(List<Map<String, Object>> data,List<String> legends,String legendKey,String xKey,String yKey){
		Map<String, Object> echartsMap=new HashMap<>();
		List<String> xAxis = getXAxis();
		String legend = "";
		Object date = "";
		String value = "";
		
		Collections.sort(xAxis);
		String[][] yAxis = new String[legends.size()][xAxis.size()];
		for(int i =0;i<legends.size();i++){
			for(int j = 0;j<xAxis.size();j++){
				yAxis[i][j]="0.00";
			}
		}
		String _l="";
		String _x="";
		
		for(Map<String, Object> map:data){
			if (!map.containsKey(legendKey)) {
				continue;
			}
			legend = map.get(legendKey).toString();
			date = map.get(xKey);
			value = map.get(yKey).toString();
			for(int i =0;i<legends.size();i++){
				for(int j = 0;j<xAxis.size();j++){
					if(StringUtil.isNullOrEmpty(data)){
						yAxis[i][j]="0.00";
					}
					_l=legends.get(i);
					_x=xAxis.get(j);
					if(_x.equals(date)&&_l.equals(legend)){
						yAxis[i][j]=value;
					}
				}
			}
		}
		echartsMap.put("legend",legends);
		echartsMap.put("xAxis",xAxis);
		echartsMap.put("series",yAxis);
		return echartsMap;
	}

	/**
	 * 组装渠道近3个月收入汇总
	 * @param data
	 * @param legends
	 * @param legendKey
	 * @param xKey
	 * @param yKey
	 * @return
	 */
	private Map<String, Object> billDataToEchatrs(List<Map<String, Object>> data,List<String> legends,String legendKey,String xKey,String yKey){
		Map<String, Object> echartsMap=new HashMap<>();
		/*List<String> xAxis = getXAxis();*/
		List<String> xAxis = new ArrayList<>();
		List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("bill_source");
		if (metaDataList != null){
			for (MetaData metaData : metaDataList){
				xAxis.add(metaData.getValue());
			}
		}
		String legend = "";
		Object date = "";
		String value = "";

		Collections.sort(xAxis);
		String[][] yAxis = new String[legends.size()][xAxis.size()];
		for(int i =0;i<legends.size();i++){
			for(int j = 0;j<xAxis.size();j++){
				yAxis[i][j]="0.00";
			}
		}
		String _l="";
		String _x="";

		for(Map<String, Object> map:data){
			legend = map.get(legendKey).toString();
			date = map.get(xKey);
			value = map.get(yKey).toString();
			for(int i =0;i<legends.size();i++){
				for(int j = 0;j<xAxis.size();j++){
					if(StringUtil.isNullOrEmpty(data)){
						yAxis[i][j]="0.00";
					}
					_l=legends.get(i);
					_x=xAxis.get(j);
					if(_x.equals(date)&&_l.equals(legend)){
						yAxis[i][j]=value;
					}
				}
			}
		}
		echartsMap.put("legend",legends);
		echartsMap.put("xAxis",xAxis);
		echartsMap.put("series",yAxis);
		return echartsMap;
	}

	private Map<String, Object> dataToEchatrs(List<Map<String, Object>> data,String legendKey,String xKey,String yKey) {
		List<String> legends=getLegend(data, legendKey);
		return dataToEchatrs(data,legends, legendKey, xKey, yKey);
	}
}
