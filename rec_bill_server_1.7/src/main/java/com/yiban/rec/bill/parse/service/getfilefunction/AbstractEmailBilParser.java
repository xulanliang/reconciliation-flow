package com.yiban.rec.bill.parse.service.getfilefunction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
import com.sun.mail.pop3.POP3Message;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.bill.parse.util.ZipUtil;
import com.yiban.rec.bill.parse.vo.EmailConfig;
import com.yiban.rec.util.DateUtil;

/**
 * @author swing
 * @date 2018年7月26日 上午10:10:56 类说明 邮件账单解析
 */
public abstract class AbstractEmailBilParser<ThirdBill> extends AbstractBillParser<ThirdBill> {

	protected EmailConfig emailConfig = new EmailConfig("pop3.163.com", 110, "pop3", "yjxzyyy2021@163.com",
			"yjxzyyy2022", "master@bankcomm.com", "110", true, null);

	private Store store = null;
	private Folder folder = null;

	/**
	 * 获取邮箱地址
	 *
	 * @return
	 */
	protected EmailConfig getEmailConfig() {
		String userName = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailUsername);
		String password = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailPassword);
		String host = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailHost,
				ProConstants.DEFAULT.get(ProConstants.ccbEmailHost));
		Integer port = Integer.parseInt(ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailPort,
				ProConstants.DEFAULT.get(ProConstants.ccbEmailPort)));
		String type = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailType,
				ProConstants.DEFAULT.get(ProConstants.ccbEmailType));
		String from = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailFrom);
		boolean isValidate = Boolean.parseBoolean(ProConfigManager.getValueByPkey(entityManager,
				ProConstants.ccbEmailValidate, ProConstants.DEFAULT.get(ProConstants.ccbEmailValidate)));
		String subject = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailSubject);
		return new EmailConfig(host, port, type, userName, password, from, "110", isValidate, subject);
	}

	// 方便测试用
	/*protected EmailConfig getEmailConfigTest() {
		String userName = "2805594118@qq.com";
		String password = "serfcaluddjjdhcj";
		String host = "pop.qq.com";
		Integer port = 110;
		String type = "pop3";
		*//*String from = "service@vip.ccb.com";*//*
		String from = "156424223@qq.com";
		boolean isValidate = true;
		String subject = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailSubject);
		return new EmailConfig(host, port, type, userName, password, from, "110", isValidate, subject);
	}*/
	protected EmailConfig getEmailConfigTest() {
		/*String userName = "dengby@gfhealthcare.com";*/
		String userName = "dengby@gfhealthcare.com";
		String password = "dengby7650534";
		String host = "mail.gfhealthcare.com";
		Integer port = 110;
		String type = "pop3";
		/*String from = "service@vip.ccb.com";*/
		String from = "webmaster@icbc.com.cn";
		boolean isValidate = true;
		String subject = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailSubject);
		return new EmailConfig(host, port, type, userName, password, from, "110", isValidate, subject);
	}

	/**
	 * 邮件类账单解析，覆盖父类的doParse 方法
	 */
	@Override
	public List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		// 先初始化email
		emailConfig = getEmailConfig();
		/*emailConfig = getEmailConfigTest();*/
//		//下载附件，解析成单个文件
		logger.info("拉取解析账单data：",date);
		File file = getLocalFile(date);
		if (file == null || file.length() == 0) {
			logger.info("本地没有下载文件，首次获取从线上拉取");
			file = this.downloadAttachment(orgCode, date);
		}
		return this.parseFile(file, orgCode);
	}

	/**
	 * 找本地文件
	 *
	 * @param date
	 * @return
	 */
	protected File getLocalFile(String date) {
		String attachMentPath = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailFilepath,
				ProConstants.DEFAULT.get(ProConstants.ccbEmailFilepath));
		attachMentPath = this.attachMentPath(attachMentPath);
		String path = attachMentPath + File.separator + date.replaceAll("-", "/");
		logger.info("path路径：",path);
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		File[] list = file.listFiles();
		if (list != null){
			logger.info(JSONObject.toJSONString(list));
			if (list.length == 1) {
				if (!list[0].isDirectory() && list[0].getName().endsWith(".zip")) {
					try {
						return decompress(list[0].getPath(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(!list[0].isDirectory()){
					return list[0];
				}
			}
			for (File f : list) {
				if (f.isDirectory() && f.listFiles().length > 0) {
					// 解压的文件中可能包含PDF文档，需要过滤
					File[] fi = f.listFiles((File p) -> {
						if (p.isFile())
							logger.info("解压文件：{}", p.getAbsolutePath());
						return p.isFile() && (p.getName().toLowerCase().endsWith("xls") || p.getName().toLowerCase().endsWith("xlsx"));
					});
					return fi != null ? fi[0] : null;
//				return f.listFiles()[1];
				}
			}
		}


		return null;
	}

	protected SearchTerm getSearch(String orgCode, String date) {
		Date begin = getStartDate(date);
		Date end = getEndDate(date);
		SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, begin);
		SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, end);
		SearchTerm sender = new FromStringTerm(emailConfig.getFrom());

		SearchTerm subject = null;
		if (StringUtils.isNotBlank(this.emailConfig.getSubjectTerm())) {
			subject = new SubjectTerm(this.emailConfig.getSubjectTerm());
		}
		SearchTerm comparisonAndTerm = null;
		if (subject == null) {
			SearchTerm[] filter = { comparisonTermGe, comparisonTermLe, sender };
			comparisonAndTerm = new AndTerm(filter);
		} else {
			SearchTerm[] filter = { comparisonTermGe, comparisonTermLe, sender, subject };
			comparisonAndTerm = new AndTerm(filter);
		}

		logger.info("查询邮件条件：发送的 开始时间:{}, 结束时间：{}， 发送人：{}", DateUtil.dateTimeToStringLine(begin),
				DateUtil.dateTimeToStringLine(end), emailConfig.getFrom());
		logger.info(new Gson().toJson(comparisonAndTerm));
		return comparisonAndTerm;
	}

	protected Date getStartDate(String date) {
		Date begin = DateUtils.transferStringToDate("yyyy-MM-dd", date);
		Calendar ca = Calendar.getInstance();
		ca.setTime(begin);
		ca.add(Calendar.DAY_OF_MONTH, 1);
		return ca.getTime();
	}

	protected Date getEndDate(String date) {
		Date end = DateUtils.transferStringToDate("yyyy-MM-dd HH:mm:ss", date + " 23:59:59");
		Calendar ca = Calendar.getInstance();
		ca.setTime(end);
		ca.add(Calendar.DAY_OF_MONTH, 1);
		return ca.getTime();
	}

	/**
	 * 所有邮件类的账单都需要先通过电子邮箱下载账附件到本地
	 *
	 * @return
	 */
	protected final File downloadAttachment(String orgCode, String date) {
		SearchTerm comparisonAndTerm = getSearch(orgCode, date);
		try {
			folder = getFolder();
			if (folder != null) {
				Message[] messages = folder.search(comparisonAndTerm);
				if (messages.length > 0) {
					Message msg = messages[0];
					if (isContainAttach((Part) msg)) {
						logger.info("开始下载附件");
						File file = saveAttachMent(msg);
						((POP3Message) msg).invalidate(true);
						return file;
					} else {
						logger.info("没有账单附件,不处理解析");
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("没有邮件接收");
					}
				}
			}
		} catch (Exception e) {
			logger.error("邮箱链接错误：",e);
		} finally {
			closeConnect();
		}
		return null;
	}

	/**
	 *
	 * 由子类实现文件格式的解析
	 *
	 * @param file
	 * @return
	 */
	protected abstract List<ThirdBill> parseFile(File file, String orgCode);

	/**
	 * 获取会话
	 *
	 * @return
	 * @throws Exception
	 */
	private Session getSessionMail() throws Exception {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", emailConfig.getHost());
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.pop3.auth", "true");
		properties.setProperty("mail.pop3.port", emailConfig.getPort()+"");
		properties.setProperty("mail.smtp.port", emailConfig.getPort()+"");
		// SSL安全连接参数
		properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.pop3.socketFactory.fallback", "true");
		properties.setProperty("mail.smtp.socketFactory.fallback", "true");
		properties.setProperty("mail.pop3.socketFactory.port", emailConfig.getPort()+"");
		properties.setProperty("mail.smtp.socketFactory.port", emailConfig.getPort()+"");
		Session sessionMail = Session.getInstance(properties);
		return sessionMail;
	}

	/**
	 * 打开收件箱
	 *
	 * @return
	 * @throws Exception
	 */
	private Folder getFolder() throws Exception {
		URLName urln = null;
		urln = new URLName(emailConfig.getType(), emailConfig.getHost(), emailConfig.getPort(), null,
				emailConfig.getUserName(), emailConfig.getPassword());
		store = getSessionMail().getStore(urln);
		store.connect();
		// 打开收件箱
//		folder = store.getFolder("INBOX");
		String foldername = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailFolder,
				ProConstants.DEFAULT.get(ProConstants.ccbEmailFolder));
		logger.info("打开收件箱为：{}", foldername);

		folder = store.getFolder(foldername);
		if (!folder.exists()) {
			logger.error(foldername + " 不存在 ~~~~~");
			return null;
		}
		// 设置只读
		folder.open(Folder.READ_WRITE);

		return folder;
	}

	/**
	 * 关闭收件箱
	 */
	private void closeConnect() {
		if (folder != null && folder.isOpen()) {
			try {
				folder.close(true);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		if (store.isConnected()) {
			try {
				store.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取邮件发送人
	 *
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private String getFrom(Message message) throws Exception {
		InternetAddress[] address = (InternetAddress[]) ((MimeMessage) message).getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		return from;
	}

	/**
	 * 获得邮件主题
	 *
	 * @param message：Message
	 * @return 邮件主题
	 */
	@SuppressWarnings("unused")
	private String getSubject(Message message) throws Exception {
		String subject = "";
		if (((MimeMessage) message).getSubject() != null) {
			subject = MimeUtility.decodeText(((MimeMessage) message).getSubject());
		}
		return subject;
	}

	/**
	 * 判断此邮件是否包含附件
	 *
	 * @param part：Part
	 * @return 是否包含附件
	 */
	private boolean isContainAttach(Part part) throws Exception {
		boolean attachflag = false;
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					attachflag = true;
				} else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1) {

						attachflag = true;
					}
					if (contype.toLowerCase().indexOf("name") != -1) {
						attachflag = true;

					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachflag = isContainAttach((Part) part.getContent());
		}
		return attachflag;
	}
	private File saveAttachMent(BodyPart part, String billDate, String sourceType) throws Exception, MessagingException{
		String fileName = "";
		String attachMentPath = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailFilepath,
				ProConstants.DEFAULT.get(ProConstants.ccbEmailFilepath));
		attachMentPath = this.attachMentPath(attachMentPath);
		MimeBodyPart m = (MimeBodyPart) part;
		Multipart content = (Multipart) m.getContent();
		for (int j = 0; j < content.getCount(); j++) {
			BodyPart bp = content.getBodyPart(j);
			String dis = bp.getDisposition();
			if ((dis != null)
					&& ((dis.equals(Part.ATTACHMENT)) || (dis.equals(Part.INLINE)))) {
				fileName = m.getFileName();
				if (fileName != null) {
					fileName = MimeUtility.decodeText(fileName);
					String dir = attachMentPath + billDate;
					return saveFile(dir, fileName, sourceType, part.getInputStream());
				}
			}
		}
		return null;
	}
	/**
	 * 保存邮件附件
	 *
	 * @param part
	 * @throws Exception
	 */
	private File saveAttachMent(Part part) throws Exception {
		Message msg = (Message) part;
		Date sendDate = msg.getSentDate();
		// 因为邮件日期和账单日期相隔一天，这里要减去一天作为文件保存的目录（保证目录日期和账单日期相同）
		String billDate = DateUtils.getSpecifiedDayBefore(sendDate).replaceAll("-", "/");
		// 将发送人当做sourceType
		String sourceType = getFrom(msg).split("@")[1];
		String fileName = "";
		// 保存附件到服务器本地
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();

			String attachMentPath = ProConfigManager.getValueByPkey(entityManager, ProConstants.ccbEmailFilepath,
					ProConstants.DEFAULT.get(ProConstants.ccbEmailFilepath));
			attachMentPath = this.attachMentPath(attachMentPath);

			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						return saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				} else if (mpart.isMimeType("multipart/*")) {
//					return saveAttachMent(mpart,billDate,sourceType);
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						return saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						return saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			return saveAttachMent((Part) part.getContent());
		}
		return null;
	}

	/**
	 * 解压zip账单文件到当前目录
	 *
	 * @param filePath
	 * @param sourceType
	 * @throws Exception
	 */
	protected static File decompress(String filePath, String sourceType) throws Exception {
		File file = new File(filePath);
		ZipUtil.unZip(file.getPath());
//		ZipFile zf = new ZipFile(file);
//		InputStream in = new BufferedInputStream(new FileInputStream(file));
//		ZipInputStream zin = new ZipInputStream(in);
//		ZipEntry ze;
//		File billFile=null;
//		if ((ze = zin.getNextEntry()) != null) {
//			if (!ze.isDirectory()) {
//				long size = ze.getSize();
//				if (size > 0) {
//					String ext = ze.getName().substring(ze.getName().lastIndexOf("."));
//					String fileName = emailConfig.getOrgCode() + "_" + sourceType + ext;
//					InputStream inputStream = zf.getInputStream(ze);
//					 billFile = new File(file.getParent(), fileName);
//					OutputStream outputStream = new FileOutputStream(billFile);
//					int len;
//					byte[] buffer = new byte[8192];
//					while (-1 != (len = inputStream.read(buffer))) {
//						outputStream.write(buffer, 0, len);
//					}
//					outputStream.close();
//					inputStream.close();
//				}
//			}
//		}
//		zin.closeEntry();
//		zf.close();
//		zin.close();
//		if (file.exists()) {
//			file.delete();
//		}
		File pFile = new File(filePath.replace(".zip", ""));
		if (pFile.exists() && pFile.isDirectory()) {
			File[] list = pFile.listFiles();
			if (list != null && list.length > 0) {
				// 过滤pdf
				for (File value : list) {
					String substring = value.getName().substring(value.getName().length() - 3);
					if ("xls".equals(substring)) {
						return value;
					}
				}
//				return list[0];
			}
		}
		return pFile;
	}

	/**
	 * 保存附件到指定目录里
	 *
	 * @param fileName：附件名称
	 * @param in：文件输入流
	 * @param filePath：邮件附件存放基路径
	 */
	@SuppressWarnings("static-access")
	private File saveFile(String filePath, String fileName, String sourceType, InputStream in) throws Exception {
		File storefile = new File(filePath);
		if (!storefile.exists()) {
			storefile.mkdirs();
		}
		try {
			File file = new File(filePath, fileName);
			BufferedInputStream bis = new BufferedInputStream(in);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			int len = -1;
			while ((len = bis.read()) != -1) {
				bos.write(len);
				bos.flush();
			}
			bos.close();
			bis.close();
			// 解压
			if (file.getName().endsWith(".zip")) {
				return this.decompress(file.getAbsolutePath(), sourceType);
			} else {
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String attachMentPath(String attachMentPath) {
		if (!attachMentPath.endsWith(File.separator)) {
			attachMentPath += File.separator;
		}
		attachMentPath += emailConfig.getUserName() + File.separator;
		return attachMentPath;
	}
}
