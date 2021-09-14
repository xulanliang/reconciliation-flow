package com.yiban.rec.web.admin;

import java.io.Closeable;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.rec.service.SelfHelpMachineService;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * @author swing
 * @date 2018年8月8日 上午9:42:02 类说明 自助结算
 */
@Controller
@RequestMapping("/admin/selfhelp/main")
public class SelfHelpMachineController extends FrameworkController {
	private final String PAY_TYPE_MZ = EnumTypeOfInt.PAT_TYPE_MZ.getValue();
	private final String PAY_TYPE_ZY = EnumTypeOfInt.PAT_TYPE_ZY.getValue();
	@Autowired
	private SelfHelpMachineService selfHelpMachineService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@GetMapping
	public String main(ModelMap model) {
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		model.put("orgNo", orgCode);
		return autoView("admin/self_help_machine");
	}

	@RestController
	@RequestMapping(value = "/admin/selfhelp")
	class OperationLogDataController extends BaseController {
		/**
		 * 加载列表数据
		 * 
		 * @return
		 */
		@GetMapping
		public WebUiPage<Map<String, Object>> findList(SelfHelpMachineService.SelfHelpMachineQuery query) {
			if (StringUtil.isNullOrEmpty(query.getOrgCode())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				query.setOrgCode(orgCode);
			}
			List<Map<String, Object>> resultList = selfHelpMachineService.findAllList(query);
			for(Map<String, Object> map : resultList) {
				map.put("advanceAmount", formatAmount(map.get("advanceAmount")));
				map.put("wxAmount", formatAmount(map.get("wxAmount")));
				map.put("wxRefund", formatAmount(map.get("wxRefund")));
				map.put("wxTotal", formatAmount(map.get("wxTotal")));
				map.put("aliAmount", formatAmount(map.get("aliAmount")));
				map.put("aliRefund", formatAmount(map.get("aliRefund")));
				map.put("aliTotal", formatAmount(map.get("aliTotal")));
				map.put("bankAmount", formatAmount(map.get("bankAmount")));
				map.put("bankRefund", formatAmount(map.get("bankRefund")));
				map.put("bankTotal", formatAmount(map.get("bankTotal")));
				map.put("cashAmount", formatAmount(map.get("cashAmount")));
				map.put("cashRefund", formatAmount(map.get("cashRefund")));
				map.put("cashTotal", formatAmount(map.get("cashTotal")));
				map.put("yibaoProvinceAmount", formatAmount(map.get("yibaoProvinceAmount")));
				map.put("yibaoCityAmount", formatAmount(map.get("yibaoCityAmount")));
				map.put("yibaoTotal", formatAmount(map.get("yibaoTotal")));
			}
			return new WebUiPage<>(resultList.size(), resultList);
		}

		private String formatAmount (Object o) {
			if(null == o) {
				return "0.00";
			}
			Double amount = Double.valueOf(o.toString());
			String result = String.format("%.2f", amount);
			return result;
		}
		
		/**
		 * 结账
		 * 
		 * @param deviceNo
		 * @return
		 */
		@PostMapping
		public ResponseResult doSettlement(SelfHelpMachineService.SelfHelpMachineQuery query) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			logger.info("医院编码 activemqHospitalId: {}, SelfHelpMachineQuery.OrgCode: {}", orgCode, query.getOrgCode());
			if(!StringUtils.equals(query.getOrgCode(), orgCode)) {
				return ResponseResult.failure("结账失败，只支持结算本院自助机！");
			}
			try {
				selfHelpMachineService.settlemnt(query);
				return ResponseResult.success("结算操作完成");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ResponseResult.failure("结账失败");
		}

		/**
		 * 汇总导出(全部设备导分门诊住院类型导出)
		 * 
		 * @param query
		 * @return
		 * @throws IOException
		 */
		@GetMapping("export/all")
		public void exportAll(SelfHelpMachineService.SelfHelpMachineQuery query, HttpServletResponse response)
				throws IOException {
			SelfHelpMachineService.SelfHelpMachineQuery q = new SelfHelpMachineService.SelfHelpMachineQuery();
			q.setBeginTime(query.getBeginTime());
			q.setEndTime(query.getEndTime());
			q.setOrgCode(query.getOrgCode());
			q.setCheckOut(query.getCheckOut());
			String fileName = query.getBeginTime() + "至" + query.getEndTime() + query.getOrgName() + "自助机结账汇总单";
			Map<String, Object> mzMap = new HashMap<>(), zyMap = new HashMap<>();
			// 分两次统计，住院门诊
			query.setPatType(PAY_TYPE_MZ);
			mzMap=selfHelpMachineService.countCheckOutAll(query);
			query.setPatType(PAY_TYPE_ZY);
			zyMap=selfHelpMachineService.countCheckOutAll(query);
		    mzMap.put("title", fileName);
		    SelfHelpMachineService. DataModel md =new SelfHelpMachineService.DataModel();
		    md.setMzModel(mzMap);
		    md.setZyModel(zyMap);
		    md.setName("汇总");
		    List<SelfHelpMachineService. DataModel> list =new ArrayList<>();
		    list.add(md);
		    // 分别单汇总
			/*List<SelfHelpMachineService.DataModel> list = new ArrayList<>();
			for (String d : query.getDeviceNos()) {
				q.setPatType(query.getPatType());
				Map<String, Object> mzMap = new HashMap<>(), zyMap = new HashMap<>();
				q.setDeviceNo(d);
				if(StringUtils.isBlank(q.getPatType())) {
					q.setPatType(PAY_TYPE_MZ);
					mzMap = selfHelpMachineService.countCheckOutAll(q);
					q.setPatType(PAY_TYPE_ZY);
					zyMap = selfHelpMachineService.countCheckOutAll(q);
				}else if(StringUtils.equals(query.getPatType(), PAY_TYPE_ZY)) {
					zyMap = selfHelpMachineService.countCheckOutAll(q);
				}else if (StringUtils.equals(query.getPatType(), PAY_TYPE_MZ)) {
					mzMap = selfHelpMachineService.countCheckOutAll(q);
				}
				mzMap.put("title", fileName);
				SelfHelpMachineService.DataModel md = new SelfHelpMachineService.DataModel();
				md.setMzModel(mzMap);
				md.setZyModel(zyMap);
				md.setName(d);
				list.add(md);
			}*/
			Workbook wb = selfHelpMachineService.generateExcel(list);
			String contentDisposition = "attachment;filename=%s.xls";
			contentDisposition = String.format(contentDisposition, URLEncoder.encode(fileName, "UTF-8"));
			response.addHeader("Content-Disposition", contentDisposition);
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			if (wb instanceof Closeable) {
				((Closeable) wb).close();
			}
		}

		/**
		 * 按每台设备分住院门诊类型导出
		 * 
		 * @param deviceNo
		 * @param model
		 * @param request
		 * @throws ParseException
		 */
		@GetMapping("export/single")
		public void exportSingle(SelfHelpMachineService.SelfHelpMachineQuery query, HttpServletResponse response)
				throws IOException {
			// 按照设备编号进行查询
			List<SelfHelpMachineService.DataModel> workbookList = new ArrayList<>();
			SelfHelpMachineService.SelfHelpMachineQuery q = new SelfHelpMachineService.SelfHelpMachineQuery();
			q.setBeginTime(query.getBeginTime());
			q.setEndTime(query.getEndTime());
			q.setOrgCode(query.getOrgCode());
			q.setCheckOut(query.getCheckOut());
			String fileName = query.getBeginTime() + "至" + query.getEndTime() + query.getOrgName() + "自助结账-单台";
			for (String d : query.getDeviceNos()) {
				q.setPatType(query.getPatType());
				Map<String, Object> mzMap = new HashMap<>(), zyMap = new HashMap<>();
				q.setDeviceNo(d);
				// 分两次统计，住院门诊
				if(StringUtils.isBlank(q.getPatType())) {
					q.setPatType(PAY_TYPE_MZ);
					mzMap = selfHelpMachineService.countCheckOutAll(q);
					q.setPatType(PAY_TYPE_ZY);
					zyMap = selfHelpMachineService.countCheckOutAll(q);
				}else if(StringUtils.equals(query.getPatType(), PAY_TYPE_ZY)) {
					// 统计住院
					zyMap = selfHelpMachineService.countCheckOutAll(q);
				}else if (StringUtils.equals(query.getPatType(), PAY_TYPE_MZ)) {
					// 统计门诊
					mzMap = selfHelpMachineService.countCheckOutAll(q);
				}
				/*q.setPatType(PAY_TYPE_MZ);
				mzMap = selfHelpMachineService.countCheckOutAll(q);
				q.setPatType(PAY_TYPE_ZY);
				zyMap = selfHelpMachineService.countCheckOutAll(q);*/
				SelfHelpMachineService.DataModel md = new SelfHelpMachineService.DataModel();
				mzMap.put("title", fileName);
				md.setMzModel(mzMap);
				md.setZyModel(zyMap);
				md.setName(d);
				workbookList.add(md);
			}

			Workbook wb = selfHelpMachineService.generateExcel(workbookList);
			String contentDisposition = "attachment;filename=%s.xls";
			contentDisposition = String.format(contentDisposition, URLEncoder.encode(fileName, "UTF-8"));
			response.addHeader("Content-Disposition", contentDisposition);
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			if (wb instanceof Closeable) {
				((Closeable) wb).close();
			}
		}

	}
}
