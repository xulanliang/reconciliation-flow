package com.yiban.rec.service.impl;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.dao.OrderSettlementDao;
import com.yiban.rec.domain.OrderSettlement;
import com.yiban.rec.service.SelfHelpMachineService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * @author swing
 * @date 2018年8月8日 下午6:12:07 类说明 自助机结算实现 基于统计的快速实现,初步完成
 *       业务需求，不考虑优雅方案及性能问题(带需求整理明确后续可以优化)
 */
@Service
public class SelfHelpMachineServiceImpl extends BaseOprService implements SelfHelpMachineService {
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrderSettlementDao orderSettlementDao;
	private final String DEFAULT_VAL = "0";
	private final String PAY_TYPE_MZ = EnumTypeOfInt.PAT_TYPE_MZ.getValue();
	private final String PAY_TYPE_ZY = EnumTypeOfInt.PAT_TYPE_ZY.getValue();
	/**
	 * 统计SQL
	 */
	private final String countSql = "" + "sum(CASE WHEN t.pay_business_type='0151' THEN 1 ELSE 0 END) rechargeCount,"
			+ "sum(CASE WHEN t.pay_business_type='0551' THEN 1 ELSE 0 END) payCount,"
			+ " sum(CASE WHEN t.pay_business_type='0151' THEN t.pay_amount ELSE 0 END) advanceAmount,"
			+ "sum(CASE WHEN t.pay_type = '0249' AND t.pay_business_type='0151' THEN 1 ELSE 0 END) wxRechargeCount,"
			+ "sum(CASE WHEN t.pay_type = '0249' AND t.pay_business_type='0551' THEN 1 ELSE 0 END) wxPayCount,"
			+ "sum(CASE WHEN t.pay_type = '0349' AND t.pay_business_type='0151' THEN 1 ELSE 0 END) aliRechargeCount,"
			+ "sum(CASE WHEN t.pay_type = '0349' AND t.pay_business_type='0551' THEN 1 ELSE 0 END) aliPayCount,"
			+ "sum(CASE WHEN t.pay_type = '0249' THEN t.pay_amount ELSE 0 END) wxAmount,"
			+ "sum(CASE WHEN t.pay_type = '0249' AND t.pay_business_type ='0651' THEN t.pay_amount ELSE 0 END) wxRefund,"
			+ "sum(CASE WHEN t.pay_type = '0349' THEN t.pay_amount ELSE 0 END) aliAmount,"
			+ "sum(CASE WHEN t.pay_type = '0349' AND t.pay_business_type ='0651' THEN t.pay_amount ELSE 0 END) aliRefund,"
			+ "sum(CASE WHEN t.pay_type = '0149' THEN t.pay_amount ELSE 0 END) bankAmount,"
			+ "sum(CASE WHEN t.pay_type = '0149' AND t.pay_business_type ='0651' THEN t.pay_amount ELSE 0 END) bankRefund,"
			+ "sum(CASE WHEN t.pay_type = '0049' THEN t.pay_amount ELSE 0 END) cashAmount,"
			+ "sum(CASE WHEN t.pay_type = '0049' AND t.pay_business_type ='0651' THEN t.pay_amount ELSE 0 END) cashRefund,"
			+ "sum(CASE WHEN t.pay_type = '0449' AND t.patient_areas = '04491' THEN t.pay_amount ELSE 0 END) yibaoProvinceAmount,"
			+ "sum(CASE WHEN t.pay_type = '0449' AND t.patient_areas = '04492' THEN t.pay_amount ELSE 0 END) yibaoCityAmount "
			+ "from t_order_upload t ";
	private final String checkOutSumSql = " sum(recharge_count) rechargeCount, " + "sum(pay_count) payCount,"
			+ "sum(advance_amount) advanceAmount," + "sum(wx_recharge_count) wxRechargeCount,"
			+ "sum(wx_pay_count) wxPayCount," + "sum(ali_recharge_count) aliRechargeCount,"
			+ "sum(ali_pay_count) aliPayCount," + "sum(wx_amount) wxAmount," + "sum(wx_refund) wxRefund,"
			+ "sum(ali_amount) aliAmount," + "sum(ali_refund) aliRefund," + "sum(bank_amount) bankAmount,"
			+ "sum(bank_refund) bankRefund," + "sum(cash_amount) cashAmount," + "sum(cash_refund) cashRefund,"
			+ "sum(yibao_province_amount) yibaoProvinceAmount," + "sum(yibao_city_amount) yibaoCityAmount "
			+ "FROM t_order_settlement t";

	private final String[] gruopColumns = new String[] { "deviceNo", "createTime", "rechargeCount", "payCount",
			"advanceAmount", "wxRechargeCount", "wxPayCount", "aliRechargeCount", "aliPayCount", "wxAmount", "wxRefund",
			"aliAmount", "aliRefund", "bankAmount", "bankRefund", "cashAmount", "cashRefund", "yibaoProvinceAmount",
			"yibaoCityAmount", "wxTotal", "aliTotal", "bankTotal", "cashTotal", "yibaoTotal" };

	private final String[] countColumns = new String[] { "rechargeCount", "payCount", "advanceAmount",
			"wxRechargeCount", "wxPayCount", "aliRechargeCount", "aliPayCount", "wxAmount", "wxRefund", "aliAmount",
			"aliRefund", "bankAmount", "bankRefund", "cashAmount", "cashRefund", "yibaoProvinceAmount",
			"yibaoCityAmount", "wxTotal", "aliTotal", "bankTotal", "cashTotal", "yibaoTotal" };

	@Override
	public List<Map<String, Object>> findAllList(SelfHelpMachineQuery query) {
		StringBuffer where = toWhereSql(query);
		String querySql = "";
		if (query.getCheckOut() == 0) {
			querySql = "SELECT t.device_no deviceNo,'0' as createTime," + countSql + "WHERE t.check_out=0 "
					+ where.toString() + " group by t.device_no";
		} else if (query.getCheckOut() == 1) {
			querySql = "SELECT t.device_no deviceNo,t.trade_date_time createTime," + checkOutSumSql + " WHERE 1=1 "
					+ where.toString() + " group by t.device_no,t.trade_date_time";
		}
		String sql = "SELECT a.*,wxAmount - wxRefund wxTotal,aliAmount - aliRefund aliTotal,"
				+ "bankAmount - bankRefund bankTotal,cashAmount - cashRefund cashTotal,"
				+ "yibaoProvinceAmount + yibaoCityAmount yibaoTotal FROM (" + querySql + ")a";
		return super.handleNativeSql(sql, gruopColumns);
	}

	@Override
	public Map<String, Object> countCheckOutAll(SelfHelpMachineQuery query) {
		StringBuffer where = toWhereSql(query);
		String querySql = "SELECT " + checkOutSumSql + " WHERE 1=1 " + where.toString();
		String sql = "SELECT a.*,wxAmount-wxRefund wxTotal,aliAmount-aliRefund aliTotal,"
				+ "bankAmount-bankRefund bankTotal,cashAmount-cashRefund cashTotal,"
				+ "yibaoProvinceAmount+ yibaoCityAmount yibaoTotal FROM (" + querySql + ")a";
		List<Map<String, Object>> totalList = super.handleNativeSql(sql, countColumns);
		Map<String, Object> restultMap = null;
		if (totalList != null && totalList.size() > 0) {
			restultMap = totalList.get(0);
		} else {
			restultMap = new HashMap<>(20);
		}
		addTotal(restultMap);
		return restultMap;
	}

	/**
	 * 根据查询条件获取记录ID(方便进行结算状态改变)
	 * 
	 * @param query
	 * @return
	 */
	public List<Long> findAllId(SelfHelpMachineQuery query) {
		StringBuffer where = toWhereSql(query);
		List<Long> result = new ArrayList<>();
		String sql = "select id from t_order_upload t where 1=1 " + where.toString();
		List<Object> resultList = super.handleNativeSql4SingleCol(sql);
		for (Object obj : resultList) {
			Long id = Long.parseLong(obj.toString());
			result.add(id);
		}
		return result;
	}

	@Transactional
	@Override
	public void settlemnt(SelfHelpMachineQuery query) {
		query.setCheckOut(0);
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tradeDateTime = fmt.format(new Date());
		query.setCheckOut(0);
		List<Long> idsList = findAllId(query);
		// 分两次统计，住院门诊
		if(StringUtils.isBlank(query.getPatType())) {
			query.setPatType(PAY_TYPE_ZY);
			settlemntZy(query, tradeDateTime);
			query.setPatType(PAY_TYPE_MZ);
			settlemntMz(query, tradeDateTime);
		}else if(StringUtils.equals(query.getPatType(), PAY_TYPE_ZY)) {
			settlemntZy(query, tradeDateTime);
		}else if (StringUtils.equals(query.getPatType(), PAY_TYPE_MZ)) {
			settlemntMz(query, tradeDateTime);
		}
		// 更新结算状态
		updateCheckOutStat(idsList);
	}

	private void settlemntMz(SelfHelpMachineQuery query, String tradeDateTime) {
		List<Map<String, Object>> mzList = this.findAllList(query);
		for (Map<String, Object> map : mzList) {
			OrderSettlement vo = converMapToBean(map);
			vo.setPatType(PAY_TYPE_MZ);
			vo.setTradeDateTime(tradeDateTime);
			if (query.getOrgCode() != null) {
				vo.setOrgCode(query.getOrgCode());
			}
			saveOrderSettlement(vo);
		}
	}

	private void settlemntZy(SelfHelpMachineQuery query, String tradeDateTime) {
		List<Map<String, Object>> zyList = this.findAllList(query);
		for (Map<String, Object> map : zyList) {
			OrderSettlement vo = converMapToBean(map);
			vo.setPatType(PAY_TYPE_ZY);
			vo.setTradeDateTime(tradeDateTime);
			if (query.getOrgCode() != null) {
				vo.setOrgCode(query.getOrgCode());
			}
			saveOrderSettlement(vo);
		}
	}

	private OrderSettlement converMapToBean(Map<String, Object> map) {
		Integer rechargeCount = Integer.parseInt(map.get("rechargeCount").toString());
		Integer payCount = Integer.parseInt(map.get("payCount").toString());
		Integer wxRechargeCount = Integer.parseInt(map.get("wxRechargeCount").toString());
		Integer wxPayCount = Integer.parseInt(map.get("wxPayCount").toString());
		Integer aliRechargeCount = Integer.parseInt(map.get("aliRechargeCount").toString());
		Integer aliPayCount = Integer.parseInt(map.get("aliPayCount").toString());

		Double advanceAmount = Double.valueOf(map.get("advanceAmount").toString());
		Double wxAmount = Double.valueOf(map.get("wxAmount").toString());
		Double aliAmount = Double.valueOf(map.get("aliAmount").toString());
		Double bankAmount = Double.valueOf(map.get("bankAmount").toString());
		Double cashAmount = Double.valueOf(map.get("cashAmount").toString());
		Double yibaoProvinceAmount = Double.valueOf(map.get("yibaoProvinceAmount").toString());
		Double yibaoCityAmount = Double.valueOf(map.get("yibaoCityAmount").toString());
		Double wxRefund = Double.valueOf(map.get("wxRefund").toString());
		Double aliRefund = Double.valueOf(map.get("aliRefund").toString());
		Double bankRefund = Double.valueOf(map.get("bankRefund").toString());
		Double cashRefund = Double.valueOf(map.get("cashRefund").toString());

		OrderSettlement vo = new OrderSettlement();
		if (map.get("deviceNo") != null) {
			vo.setDeviceNo(map.get("deviceNo").toString());
		}
		vo.setRechargeCount(rechargeCount);
		vo.setPayCount(payCount);
		vo.setWxRechargeCount(wxRechargeCount);
		vo.setWxPayCount(wxPayCount);
		vo.setAliRechargeCount(aliRechargeCount);
		vo.setAliPayCount(aliPayCount);
		vo.setAdvanceAmount(advanceAmount);
		vo.setWxAmount(wxAmount);
		vo.setAliAmount(aliAmount);
		vo.setBankAmount(bankAmount);
		vo.setCashAmount(cashAmount);
		vo.setYibaoProvinceAmount(yibaoProvinceAmount);
		vo.setYibaoCityAmount(yibaoCityAmount);
		vo.setWxRefund(wxRefund);
		vo.setAliRefund(aliRefund);
		vo.setBankRefund(bankRefund);
		vo.setCashRefund(cashRefund);
		return vo;
	}

	// 合并记录
	private void addTotal(Map<String, Object> map) {
		if (map == null) {
			return;
		}
		if (map.get("rechargeCount") == null) {
			map.put("rechargeCount", DEFAULT_VAL);
		}
		if (map.get("payCount") == null) {
			map.put("payCount", DEFAULT_VAL);
		}
		if (map.get("advanceAmount") == null) {
			map.put("advanceAmount", DEFAULT_VAL);
		}
		if (map.get("wxRechargeCount") == null) {
			map.put("wxRechargeCount", DEFAULT_VAL);
		}
		if (map.get("wxPayCount") == null) {
			map.put("wxPayCount", DEFAULT_VAL);
		}
		if (map.get("aliRechargeCount") == null) {
			map.put("aliRechargeCount", DEFAULT_VAL);
		}
		if (map.get("aliPayCount") == null) {
			map.put("aliPayCount", DEFAULT_VAL);
		}

		if (map.get("wxAmount") == null) {
			map.put("wxAmount", DEFAULT_VAL);
		}
		if (map.get("aliAmount") == null) {
			map.put("aliAmount", DEFAULT_VAL);
		}
		if (map.get("bankAmount") == null) {
			map.put("bankAmount", DEFAULT_VAL);
		}
		if (map.get("cashAmount") == null) {
			map.put("cashAmount", DEFAULT_VAL);
		}
		if (map.get("yibaoProvinceAmount") == null) {
			map.put("yibaoProvinceAmount", DEFAULT_VAL);
		}
		if (map.get("yibaoCityAmount") == null) {
			map.put("yibaoCityAmount", DEFAULT_VAL);
		}
		if (map.get("wxRefund") == null) {
			map.put("wxRefund", DEFAULT_VAL);
		}
		if (map.get("aliRefund") == null) {
			map.put("aliRefund", DEFAULT_VAL);
		}
		if (map.get("bankRefund") == null) {
			map.put("bankRefund", DEFAULT_VAL);
		}
		if (map.get("cashRefund") == null) {
			map.put("cashRefund", DEFAULT_VAL);
		}

		if (map.get("wxTotal") == null) {
			map.put("wxTotal", DEFAULT_VAL);
		}

		if (map.get("aliTotal") == null) {
			map.put("aliTotal", DEFAULT_VAL);
		}

		if (map.get("bankTotal") == null) {
			map.put("bankTotal", DEFAULT_VAL);
		}

		if (map.get("cashTotal") == null) {
			map.put("cashTotal", DEFAULT_VAL);
		}

		if (map.get("yibaoTotal") == null) {
			map.put("yibaoTotal", DEFAULT_VAL);
		}

		Double wxAmount = Double.valueOf(map.get("wxAmount").toString());
		Double aliAmount = Double.valueOf(map.get("aliAmount").toString());
		Double bankAmount = Double.valueOf(map.get("bankAmount").toString());
		Double cashAmount = Double.valueOf(map.get("cashAmount").toString());
		Double yibaoProvinceAmount = Double.valueOf(map.get("yibaoProvinceAmount").toString());
		Double yibaoCityAmount = Double.valueOf(map.get("yibaoCityAmount").toString());
		Double wxRefund = Double.valueOf(map.get("wxRefund").toString());
		Double aliRefund = Double.valueOf(map.get("aliRefund").toString());
		Double bankRefund = Double.valueOf(map.get("bankRefund").toString());
		Double cashRefund = Double.valueOf(map.get("cashRefund").toString());
		Double amountTotal = wxAmount + aliAmount + bankAmount + cashAmount + yibaoProvinceAmount + yibaoCityAmount;
		Double refundTotal = wxRefund + aliRefund + bankRefund + cashRefund;
		map.put("amountTotal", amountTotal);
		map.put("refundTotal", refundTotal);
		map.put("allTotal", amountTotal - refundTotal);
	}

	@Override
	public void updateCheckOutStat(List<Long> ids) {
		String idStr = StringUtils.join(ids, ",");
		String sql = String.format("update t_order_upload t set t.check_out=1 where t.id in(%s)", idStr);
		super.execute(sql);

	}

	/**
	 * 根据条件拼装SQL
	 * 
	 * @param query
	 * @return
	 */
	private StringBuffer toWhereSql(SelfHelpMachineQuery query) {
		StringBuffer where = new StringBuffer(100);
		/*if (StringUtils.isNotBlank(query.getOrgCode())) {
			Organization org = organizationService.findByCode(query.getOrgCode());
			Set<String> orgSet = new HashSet<>();
			orgSet.add(query.getOrgCode());
			if (org.getChildren().size() > 0) {
				for (Organization child : org.getChildren()) {
					orgSet.add(child.getCode());
				}
			}
			String[] orgArr = (String[]) orgSet.toArray(new String[0]);
			String inStr = wrapDeviceStr(orgArr);
		}*/
		where.append(" AND t.org_code in(").append(query.getOrgCode()).append(")");
		if (StringUtils.isNotBlank(query.getPatType())) {
			where.append(" AND t.pat_type ='").append(query.getPatType()).append("'");
		}
		if(query.getCheckOut() == 0) {
			where.append(" AND t.check_out ='").append(query.getCheckOut()).append("'");
		}
		if (StringUtils.isNotBlank(query.getDeviceNo())) {
			where.append(" AND t.device_no ='").append(query.getDeviceNo()).append("'");
		}
		if (query.getDeviceNos() != null && query.getDeviceNos().length > 0) {
			where.append(" AND t.device_no in(").append(wrapDeviceStr(query.getDeviceNos())).append(")");
		}
		if (StringUtils.isNotBlank(query.getBeginTime())) {
			where.append(" AND t.trade_date_time >='").append(query.getBeginTime()).append(" 00:00:00").append(" '");
		}
		if (StringUtils.isNotBlank(query.getEndTime())) {
			where.append(" AND t.trade_date_time <='").append(query.getEndTime()).append(" 23:59:59").append("'");
		}
		return where;
	}

	/**
	 * 加载模板
	 * 
	 * @param path
	 * @return
	 */
	private Workbook loadWorkBookTemplate() {
		Resource resource = new ClassPathResource("jsper/excelTemplate.xlsx");
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(resource.getInputStream());
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}

	/**
	 * 渲染模板
	 * 
	 * @param workbook
	 * @param m1
	 * @param m2
	 */
	private void resolveWorkBook(Workbook workbook, Map<String, Object> m1, Map<String, Object> m2) {
		String beginChat = "{";
		String endChat = "}";
		String defaultVal = "0";
		Sheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		Map<String, Object> model = m1;
		for (int index = 0; index < rows; index++) {
			Row row = sheet.getRow(index);
			if (row == null) {
				model = m2;
			} else {
				int cellNum = row.getLastCellNum();
				for (int j = 0; j < cellNum; j++) {
					Cell cell = row.getCell(j);
					if (cell != null) {
						String v = cell.getStringCellValue();
						if (v != null && v.length() > 0) {
							v = v.trim();
							if (v.contains(beginChat) || v.contains(endChat)) {
								if (model == null) {
									cell.setCellValue(defaultVal);
								} else {
									v = v.substring(v.indexOf(beginChat) + 1, v.lastIndexOf(endChat));
									Object vv = model.get(v);
									if (vv != null) {
										cell.setCellValue(vv.toString());
									} else {
										cell.setCellValue(defaultVal);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 将渲染后的多个表格写入一个表格中
	 * 
	 * @param list
	 * @return
	 */
	public Workbook generateExcel(List<DataModel> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		// 加载excel模板
		for (int i = 0; i < list.size(); i++) {
			DataModel w = list.get(i);
			HSSFSheet newSheet = wb.createSheet(w.getName());
			Workbook workbook = loadWorkBookTemplate();
			// 模型渲染模板
			resolveWorkBook(workbook, w.getMzModel(), w.getZyModel());
			Sheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getLastRowNum();
			// 将模板复制到新的excel工作簿中
			for (int index = 0; index < rows; index++) {
				Row row = sheet.getRow(index);
				Row newRow = newSheet.createRow(index);
				if (row != null) {
					if (index == 0) {
						HSSFCellStyle cellStyle = wb.createCellStyle();
						cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
						cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						newRow.setRowStyle(cellStyle);
						newSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
					}
					int cellNum = row.getLastCellNum();
					for (int j = 0; j < cellNum; j++) {
						Cell cell = row.getCell(j);
						Cell newCell = newRow.createCell(j);
						if (cell != null) {
							String v = cell.getStringCellValue();
							if (v != null && v.length() > 0) {
								newCell.setCellValue(v);
							}
						}
					}
				}

			}
			// 关闭
			if (workbook instanceof Closeable) {
				try {
					((Closeable) workbook).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return wb;
	}

	@Transactional
	@Override
	public void saveOrderSettlement(OrderSettlement orderSettlement) {
		orderSettlementDao.save(orderSettlement);
	}

	private String wrapDeviceStr(String[] deviceNos) {
		StringBuffer sb = new StringBuffer(20);
		for (String s : deviceNos) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append("'").append(s).append("'");
		}
		return sb.toString();
	}

}
