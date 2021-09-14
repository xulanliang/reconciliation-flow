package com.yiban.rec.bill.parse.service.standardbill.impl.juhe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.service.changefileformat.impl.ExcelFileParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.impl.JuheBillParser;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import net.sf.json.JSONObject;

/**
 * 聚合支付接口账单：建设银行账单解析
 * 
 * 1.调下载接口，2.到共享盘符下拿文件
 * 
 * @author clearofchina
 *
 */
public class JuheInterfaceBillParser extends JuheBillParser<ThirdBill> {
	private final String FILE_TYPE = ".txt";
	private final String FILE_X_TYPE = ".xlsx";
	private String acount;
	private String getFileNameUrl;
	private String downloadUrl;
	private String sharePath;
	private String ip;
	private String userName;
	private String passWord;
	// 要下载到本地的地址
	private String downloadPath;
	//获取方式(yc,nginx)
	private String getType;

	public void init() {
		acount = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheAcount);
		//账单预下载接口地址
		getFileNameUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheGetFileNameUrl);
		//账单下载接口地址
		downloadUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheDownloadUrl);
		// 聚合支付下载到的地址（如果和综合支付部署不在一台机子上则需要远程拉取）
		sharePath = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheSharePath);
		// 下载到本地的地址
		downloadPath = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheDownloadPath);
		//账单文件所在地址服务器ip
		ip = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheIpPath);
		//用户名
		userName = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheUserName);
		//密码
		passWord = ProConfigManager.getValueByPkey(entityManager, ProConstants.juhePassWord);
		
		getType = ProConfigManager.getValueByPkey(entityManager, ProConstants.juheGetType);
	}

	/**
	 * 解析账单返回账单列表
	 */
	@Override
	public List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		List<ThirdBill> bills = null;
		try {
			// 初始化参数
			init();
			
			String payFileName = this.doPost(date, 0, orgCode);

			if (StringUtils.isNotEmpty(payFileName)) {
				logger.info("下载文件路径：" + downloadPath + File.separator + payFileName);
				bills = this.parserBill(this.decompress(new File(downloadPath + File.separator + payFileName)),
						orgCode);
			}
			// 删除本地聚合支付账单文件
			//FileSaveUtil.delFile(downloadPath + File.separator + payFileName);
		} catch (Exception e) {
			logger.error("建行聚合支付拉取异常" + e.getMessage());
		}
		return bills;
	}

	/*public static void main(String[] args) throws Exception {
		
		 * List<ThirdBill> list = parserBill(decompress(new File("D:\\test" +
		 * File.separator +
		 * "SHOP.105307000004898.20190329.20190329.20190409142316273.299y.zip")), null);
		 * for(ThirdBill v:list) { System.out.println(v.getPayFlowNo()); }
		 
		//getFileFromURL1("admin","123456","192.168.24.77","192.168.24.77/test/SHOP.105307000004898.20190329.20190329.20190409142316273.299y.zip",null);
		// FileSaveUtil.delAllFile("D:\\test"+ File.separator);
		//getBillZip1("SHOP.105307000004898.20190329.20190329.20190409142316273.299y.zip");
	}*/
	
	// url=admin:123456@192.168.24.77/bank/20190128-039130001.txt
	public void getFileFromURL(String userName, String passWord, String ip, String url, String orgCode)
			throws Exception {
		String smbHead = "smb://";
		InputStream in = null;
		OutputStream out = null;
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(ip, userName, passWord);
		try {
			logger.info("远程路径:" + smbHead + url);
			SmbFile remoteFile = new SmbFile(smbHead + url, auth);
			String fileName = remoteFile.getName();
			logger.info("下载到本地:" + downloadPath + File.separator + fileName);
			File localFile = new File("" + File.separator + fileName);
			if (!localFile.getParentFile().exists()) {
				localFile.getParentFile().mkdirs();
			}
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			byte[] buffer = new byte[1024];
			while (in.read(buffer) != -1) {
				out.write(buffer);
				buffer = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				throw new Exception(e.getMessage());
			}
		}
	}
	
	public void getBillZip(String fileName) throws Exception {
		try {
			logger.info("下载到本地:" + downloadPath + File.separator + fileName);
			HttpClientUtil.download(sharePath + "/" + fileName, downloadPath+ File.separator + fileName);
			//HttpClientUtil.download(url, "E:\\"+ File.separator + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public String doPost(String date, int type, String orgCode) {
		String fileName = null;
		// 获取支付流水
		JSONObject res = doPostPredownload(date, type);
		if (res == null || !"000000".equals(res.getString("returnCode"))
				|| StringUtils.isEmpty(res.getString("fileName"))) {
			logger.error("调用order/ccb/bill/predownload接口返回异常，" + res);

			return null;
		}
		fileName = res.getString("fileName");
		res = doPostDownload(fileName);
		if (res == null || !"000000".equals(res.getString("returnCode"))) {
			logger.error("调用order/ccb/bill/download 接口返回异常，" + res);
			return null;
		} else {// 下载到本地
			try {
				switch (getType) {
				case "nginx":
					getBillZip(fileName);
					break;
				default:
					getFileFromURL(userName, passWord, ip, sharePath + "/" + fileName, orgCode);
					break;
				}
				
			} catch (Exception e) {
				logger.error("下载到本地出错|{} ", e);
				return null;
			}
		}
		logger.info("文件名：" + fileName + "-- 返回结果：" + res);
		return fileName;
	}

	/**
	 * 请求下载文件
	 * 
	 * @param date 账单日期
	 * @param type 0：支付流水；1：退款流水 （实际上下载的文件都是一样的）
	 * @return
	 */
	public JSONObject doPostPredownload(String date, int type) {
		JSONObject json = new JSONObject();
		json.put("account", acount);
		json.put("requestSn", System.currentTimeMillis());
		json.put("billDate", date.replaceAll("-", ""));
		// 0:未结流水,1:已结流水
		json.put("kind", 1);
		// 1：txt（默认），2：excel（一点接商户不支持excel文件格式下载）
		json.put("fileType", 1);
		// 0：支付流水；1：退款流水
		json.put("type", type);
		json.put("nOrderBy", 1);
		json.put("status", 3);
		json.put("orderId", "");
		json.put("posCode", "");
		json.put("billFlag", "");

		logger.info("获取文件名参数:" + json.toString() + ", getFileNameUrl = " + getFileNameUrl);
		return JSONObject.fromObject(HttpClientUtil.doPostJson(getFileNameUrl, json.toString()));
	}

	public JSONObject doPostDownload(String fileName) {
		JSONObject json = new JSONObject();
		json.put("account", acount);
		json.put("requestSn", System.currentTimeMillis());
		json.put("source", fileName); // 要下载的文件名
		json.put("filePath", "merchant/shls");
		json.put("localRemote", 0);

		logger.info("请求下载文件参数:" + json.toString() + ", downloadUrl = " + downloadUrl);
		return JSONObject.fromObject(HttpClientUtil.doPostJson(downloadUrl, json.toString()));
	}

	/**
	 * 读取TXT文件内容，转换为账单实体对象
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public final List<ThirdBill> parserBill(File file, String orgCode) throws Exception {
		List<ThirdBill> thirdBills = new ArrayList<>();
		if (file != null) {
			try (InputStream in = new FileInputStream(file)) {
				List<String> lines = null;
				switch (FILE_TYPE) {
				case ".txt":
					lines = readFileLines(in);
					break;
				case ".xlsx":
					lines = new ExcelFileParser().fileToList(file);
					break;
				}
				thirdBills = convert(lines, orgCode);
			} finally {
				if (file.exists()) {
					file.getAbsoluteFile().delete();
				}
			}
		}
		return thirdBills;
	}

	private final List<ThirdBill> convert(List<String> lines, String orgCode) {
		List<ThirdBill> thirdBills = new ArrayList<>();
		if (lines != null && lines.size() > 2) {
			for (int i = 2, len = lines.size(); i < len; i++) {
				ThirdBill vo = convertToBean(lines.get(i), orgCode);
				if(vo==null) continue;
				thirdBills.add(vo);
			}
		}
		// logger.info("下载行数：" + thirdBills.size());
		return thirdBills;
	}

	/**
	 * 将文本信息转换成bean对象
	 * 
	 * @param line
	 * @return
	 */
	public ThirdBill convertToBean(String line, String orgCode) {
		ThirdBill thirdBill = null;
		if (StringUtils.isNotEmpty(line)) {
			// 21列
			String[] arr = line.split("\t");
			if (arr.length > 0) {
				thirdBill = new ThirdBill();
				thirdBill.setBillSource(EnumTypeOfInt.BILL_SOURCE_CCB.getValue());
				thirdBill.setOrgNo(orgCode);
				// 支付类型：1649 聚合支付
				if (arr[13].equals("支付宝")) {
					thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue());
				} else if (arr[13].equals("微信")) {
					thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue());
				} else {
					thirdBill.setPayType(EnumTypeOfInt.PAY_TYPE_BANK.getValue());
				}
				thirdBill.setRecPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
				thirdBill.setPayFlowNo(arr[4]);
				// 交易金额
				BigDecimal amount = new BigDecimal(arr[9]);
				thirdBill.setPayAmount(amount);
				thirdBill.setTradeDatatime(DateUtils.transferStringToDate("yyyy-MM-dd HH:mm:ss", arr[0]));
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
					// 退费
					thirdBill.setOrderState("0256");
				} else {
					// 缴费
					thirdBill.setOrderState("0156");
				}
			}
		}
		return thirdBill;
	}

	/**
	 * 读取TXT文件所有行内容
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public final List<String> readFileLines(InputStream in) throws Exception {
		return IOUtils.readLines(in, "UTF-8");
	}

	/**
	 * 解压zip账单文件到当前目录
	 * 
	 * @param filePath
	 * @param sourceType
	 * @throws Exception
	 * 
	 */
	private final File decompress(File file) throws Exception {
		ZipFile zf = new ZipFile(file);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry ze;
		File billFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			while ((ze = zin.getNextEntry()) != null) {
				if (!ze.isDirectory()) {
					if(ze.getName().contains("YH")) continue;
					if (ze.getName().endsWith(FILE_TYPE) && !ze.getName().contains(FILE_X_TYPE)) {
						String ext = ze.getName().substring(ze.getName().lastIndexOf(".") + 1);
						String fileName = ze.getName() + "_" + System.currentTimeMillis() + "." + ext;
						inputStream = zf.getInputStream(ze);
						billFile = new File(file.getParent(), fileName);
						outputStream = new FileOutputStream(billFile);
						int len;
						byte[] buffer = new byte[8192];
						while (-1 != (len = inputStream.read(buffer))) {
							outputStream.write(buffer, 0, len);
						}
					}
				}
			}
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					// logger.error("关闭文件IO异常 ：" + e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					// logger.error("关闭文件IO异常 ：" + e);
				}
			}
			try {
				zin.closeEntry();
			} catch (Exception e) {
				// logger.error("关闭文件IO异常 ：" + e);
			}
			try {
				zf.close();
			} catch (Exception e) {
				// logger.error("关闭文件IO异常 ：" + e);
			}
			try {
				zin.close();
			} catch (Exception e) {
				// logger.error("关闭文件IO异常 ：" + e);
			}
		}
//		if (file.exists()) {
//			file.delete();
//		}
		return billFile;
	}
	
	@Override
	protected void clearBill(String orgCode, String date, EntityManager entityManager, String payType) {
		String sDate = date + " 00:00:00";
		String eDate = date + " 23:59:59";

		String tableName = " t_thrid_bill ";
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM " + tableName);
		sb.append(" WHERE org_no = '" + orgCode + "'");
		sb.append(" AND Trade_datatime >= '" + sDate + "'");
		sb.append(" AND Trade_datatime <= '" + eDate + "'");
		sb.append(" AND bill_source = '"+EnumTypeOfInt.BILL_SOURCE_CCB.getValue()+"' ");
		String sql = sb.toString();
		logger.info("clearBill sql = " + sql);

		Session session = entityManager.unwrap(org.hibernate.Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		int count = query.executeUpdate();
		logger.info("clearBill count = " + count);
	}
}
