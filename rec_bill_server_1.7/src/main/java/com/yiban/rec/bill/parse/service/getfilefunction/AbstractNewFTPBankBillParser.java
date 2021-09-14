package com.yiban.rec.bill.parse.service.getfilefunction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.export.util.ExcelUtils;
import com.yiban.rec.bill.parse.service.changefileformat.impl.CsvFileParser;
import com.yiban.rec.bill.parse.service.changefileformat.impl.ExcelFileParser;
import com.yiban.rec.bill.parse.service.changefileformat.impl.PdfFileParser;
import com.yiban.rec.bill.parse.service.changefileformat.impl.TextFileParser;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.FtpUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.bill.parse.util.ZipUtil;
import com.yiban.rec.bill.parse.vo.FTPConfig;
import com.yiban.rec.domain.ThirdBill;

public abstract class AbstractNewFTPBankBillParser<T> extends AbstractBillParser<ThirdBill> {

	private final static String CHARSET = "UTF-8";
	/**
	 * 文件类型
	 * @author admin
	 *
	 */
	protected enum FileTypeEnum{
		TXT,EXCEL,CSV,PDF;
	}
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 对账日期
	 */
	protected String date;
	/**
	 * 机构编码
	 */
	protected String orgCode;
	
	private List<String> lines = new ArrayList<>();
	
	@Override
	public List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		this.date=date;
		this.orgCode=orgCode;
		String billSource = getBillSource();
		if(StringUtils.isNotBlank(billSource)&&!StringUtils.equals(billSource, EnumTypeOfInt.BILL_SOURCE_SELF.getValue())){
			Set<String> billSources = new HashSet<>();
			billSources.add(billSource);
			super.setBillSource(billSources);
		}
		String[] shopNos = getShopNo();
		if(shopNos!=null&&shopNos.length>0) {
			List<ThirdBill> list=new ArrayList<>();
			for(int i=0;i<shopNos.length;i++) {
				File file = getLocalFile(getFileName(date,shopNos[i]));
				if(file!=null&&file.exists()&&file.length()>0){
					file.delete();
				}
				file = downLoadFtpFile(getFileName(date,shopNos[i]));
				list.addAll(readFileByLines(file));
			}
			return list;
		}else {
			File file = getLocalFile();
			if(file==null||!file.exists()||file.length()<=0){
				file = downLoadFtpFile();
			}
			return readFileByLines(file);	
		}
	}
	/**
	 * 获取FTP信息
	 * @return
	 */
	protected FTPConfig getFtpConfig() {
//		String host=CommonPropertiesUtils.getValue("bank.ftp.host","");
		String host = ProConfigManager.getValueByPkey(entityManager, ProConstants.bankFtpHost, "");
//		Integer port=Integer.parseInt(CommonPropertiesUtils.getValue("bank.ftp.port","21"));
		Integer port=Integer.parseInt(ProConfigManager.getValueByPkey(entityManager, ProConstants.bankFtpPort, "21"));
//		String username=CommonPropertiesUtils.getValue("bank.ftp.username","anonymous");
		String username=ProConfigManager.getValueByPkey(entityManager, ProConstants.bankFtpUsername, "anonymous");
//		String password=CommonPropertiesUtils.getValue("bank.ftp.password","");
		String password=ProConfigManager.getValueByPkey(entityManager, ProConstants.bankFtpPassword, "anonymous");
//		String ftpPath=CommonPropertiesUtils.getValue("bank.ftp.path","");
		String ftpPath=ProConfigManager.getValueByPkey(entityManager, ProConstants.bankFtpPath, "");
//		String savePath=CommonPropertiesUtils.getValue("bank.bill.path","C:\\BankBillFTP");
		String savePath=ProConfigManager.getValueByPkey(entityManager, ProConstants.bankBillPath, "C:\\BankBillFTP");
		return new FTPConfig(host, port, username, password, ftpPath, savePath);
	}
	/**
	 * 获取ftp文件名称
	 * @param date
	 * @return
	 */
	protected abstract String getFileName(String date);
	
	/**
	 * 获取多个ftp文件名称
	 * @param date
	 * @return
	 */
	protected abstract String getFileName(String date,String name);
	
	protected abstract String[] getShopNo();
	
	/**
	 * 开始几行不解析
	 * @param lineCount
	 * @return
	 */
	protected abstract int onStartOffset(int lineCount);
	/**
	 * 结尾几行不解析
	 * @param lineCount
	 * @return
	 */
	protected abstract int onEndOffset(int lineCount);
	/**
	 * 获取文本分隔符
	 * @return
	 */
	protected abstract String getSeparatorChar();
	/**
	 * 一行一行解析数据
	 * @param strArr
	 * @param index
	 * @return
	 */
	protected abstract ThirdBill parseByLine(String[] strArr, int index) ;
	/**
	 * 渠道
	 * @return
	 */
	protected String getBillSource(){
		return EnumTypeOfInt.BILL_SOURCE_SELF.getValue();
	}
	/**
	 * 下载Ftp文件
	 * @return
	 */
	protected FileTypeEnum getFileType(String fileName){
		fileName = fileName.toLowerCase();
		if(fileName.endsWith(".txt")){
			return FileTypeEnum.TXT;
		}else if(fileName.endsWith(".csv")){
			return FileTypeEnum.CSV;
		}else if(fileName.endsWith(".pdf")){
			return FileTypeEnum.PDF;
		}else if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){
			return FileTypeEnum.EXCEL;
		}
		return FileTypeEnum.TXT;
	}
	/**
	 * 获取本地文件是否存在
	 * @return
	 */
	protected File getLocalFile(){
		String localPath = getFtpConfig().getSavePath();
		String fileName = getFileName(date);
		return getLocalFile(localPath,fileName);
	}
	protected File getLocalFile(String fileName){
		String localPath = getFtpConfig().getSavePath();
		return getLocalFile(localPath,fileName);
	}
	protected File getLocalFile(String localPath, String fileName) {
		File file = new File(localPath,fileName);
		if(!file.exists()){
			return null;
		}
		if(fileName.endsWith(".zip")){
			try {
				return decompress(file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	protected File downLoadFtpFile(){
		return downLoadFtpFile(getFtpConfig(),getFileName(date));
	}
	
	protected File downLoadFtpFile(String fileName){
		return downLoadFtpFile(getFtpConfig(),fileName);
	}
	
	protected File downLoadFtpFile(FTPConfig ftpConfig,String fileName){
		if(StringUtils.isBlank(ftpConfig.getHost())){
			return null;
		}
		File file = FtpUtil.downloadFtpFile(ftpConfig.getHost(), ftpConfig.getUsername(), ftpConfig.getPassword(), ftpConfig.getPort(), ftpConfig.getFtpPath(), ftpConfig.getSavePath(), fileName);
		if(file.exists()&&file.getName().endsWith("zip")){
			try {
				return decompress(file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	/**
	 * 分割字符串 生成字符串数组
	 * @param str
	 * @param separator
	 * @return
	 */
	protected String[] splitStr(String str,String separator){
		return str.split(separator);
	}
	
	
	/**
	 * 一行一行读取文件并解析
	 * @param file
	 * @return
	 */
	protected List<ThirdBill> readFileByLines(File file){
		logger.info("### {} 一行一行解析文件 ",orgCode);
		List<ThirdBill> thirdBills = new ArrayList<>();
		if(file==null||!file.exists()){
			return thirdBills;
		}
		List<String> lines = getLines(file);
		if(lines==null){
			return thirdBills;
		}
		int lineCount = lines.size();
		int start = onStartOffset(lineCount);
		int end = onEndOffset(lineCount);
		ThirdBill thirdBill=null;
		String separator = getSeparatorChar();
		if(separator==null||separator.equals("")){
			separator=",";
		}
		String line = "";
		for (int i = start; i < lineCount-end; i++) {
			line = lines.get(i);
			if(StringUtils.isBlank(line)){
				continue;
			}
			thirdBill=parseByLine(splitStr(line,separator),i);
			if(thirdBill!=null){
				if(StringUtils.isBlank(thirdBill.getOrgNo())){
					thirdBill.setOrgNo(orgCode);
				}
				if(StringUtils.isBlank(thirdBill.getBillSource())){
					thirdBill.setBillSource(StringUtils.isBlank(getBillSource())?EnumTypeOfInt.BILL_SOURCE_SELF.getValue():getBillSource());
				}
				if(StringUtils.isBlank(thirdBill.getPayType())){
					thirdBill.setPayType(StringUtils.isBlank(thirdBill.getRecPayType())?EnumTypeOfInt.PAY_TYPE_BANK.getValue():thirdBill.getRecPayType());
				}
				if(StringUtils.isBlank(thirdBill.getRecPayType())){
					thirdBill.setRecPayType(StringUtils.isBlank(thirdBill.getPayType())?EnumTypeOfInt.PAY_TYPE_BANK.getValue():thirdBill.getPayType());
				}
				thirdBill.setCreatedDate(new Date());
				thirdBills.add(thirdBill);
			}
		}
		logger.info("### 共：{}条数据",thirdBills.size());
		return thirdBills;
	}
	protected String getCharset(){
		return CHARSET;
	}
	/**
	 * 读取每一行数据放入到集合中
	 * @param file
	 * @return
	 */
	private List<String> getLines(File file) {
		FileTypeEnum fileType = getFileType(file.getName());
		switch (fileType) {
			case TXT:
				lines=new TextFileParser(getCharset()).fileToList(file);
				break;
			case CSV:
				lines=new CsvFileParser(getCharset()).fileToList(file);
				break;
			case EXCEL:
				lines=new ExcelFileParser().fileToList(file);
				break;
			case PDF:
				lines=new PdfFileParser().fileToList(file);
				break;
			default:
				break;
		}
		return lines;
	}
	
	/**
	 * 获取excel每行数据
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unused")
	private List<String> getExcelLines(File file) {
		List<String> lines = new ArrayList<>();
		try {
			String[] data = ExcelUtils.getDataFromExcel(file);
			if(data!=null&&data.length>0){
				lines= Arrays.asList(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
	/**
	 * 解压zip账单文件到当前目录
	 * @param filePath
	 * @param sourceType
	 * @throws Exception
	 */
	private File decompress(String filePath) throws Exception {
		File file = new File(filePath);
		ZipUtil.unZip(file.getPath());
		File pFile = new File(filePath.replace(".zip",""));
		if(pFile.exists()&&pFile.isDirectory()){
			File[] list = pFile.listFiles();
			if(list!=null&&list.length>0){
				return list[0];
			}
		}
		return pFile;
	}
	/**
	 * 获取某一行某一列的值
	 * @param row
	 * @param column
	 * @return
	 */
	protected String getColumns(int row,int column){
		if(row<0||column<0){
			return "";
		}
		if(row>(lines.size()-1)){
			return "";
		}
		String line = lines.get(row);
		if(StringUtils.isBlank(line)){
			return "";
		}
		String[] arr = line.split(getSeparatorChar());
		if(column>(arr.length-1)){
			return "";
		}
		return arr[column];
	}
}
