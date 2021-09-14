package com.yiban.rec.bill.parse.service.standardbill.impl.juhe;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import net.sf.json.JSONObject;

/**
 * 聚合支付账单解析入库：不需要下载到本地，直接读取远程共享文件夹
 */
public class JuheBillParserNoSamba extends JuheInterfaceBillParser {

	private final String SUCCESS_CODE = "000000";

	/**
	 * 重写获取文件名的方法：不需要下载到本地，直接读取文件内容
	 */
	@Override
	public String doPost(String date, int type, String orgCode) {
		String fileName = null;
		// 获取文件名
		JSONObject res = doPostPredownload(date, type);
		if (res == null || !SUCCESS_CODE.equals(res.getString("returnCode"))
				|| StringUtils.isEmpty(res.getString("fileName"))) {
			logger.error("调用order/ccb/bill/predownload接口返回异常，" + res);

			return null;
		}
		fileName = res.getString("fileName");
		res = doPostDownload(fileName);
		if (res == null || !SUCCESS_CODE.equals(res.getString("returnCode"))) {
			logger.error("调用order/ccb/bill/download 接口返回异常，" + res);
			return null;
		}

		logger.info("文件名：" + fileName + "-- 返回结果：" + res);
		return fileName;
	}

	@Override
	protected void clearBill(String orgCode, String date, EntityManager entityManager, String payType) {
		String sDate = date + " 00:00:00";
		String eDate = date + " 23:59:59";

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM t_thrid_bill ");
		sb.append(" WHERE org_no = '" + orgCode + "'");
		sb.append(" AND Trade_datatime >= '" + sDate + "'");
		sb.append(" AND Trade_datatime <= '" + eDate + "'");
		sb.append(" AND bill_source = 'self' ");
		sb.append(" AND rec_pay_type = '" + payType + "' ");
		String sql = sb.toString();
		logger.info("clearBill sql = " + sql);

		Session session = entityManager.unwrap(org.hibernate.Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		int count = query.executeUpdate();
		logger.info("clearBill count = " + count);
	}

}
