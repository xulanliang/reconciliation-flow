package com.yiban.rec.emailbill.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.pop3.POP3Message;
import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.vo.EmailConfig;
import com.yiban.rec.emailbill.service.AttachmentParser;
import com.yiban.rec.emailbill.service.EmailBillService;
import com.yiban.rec.emailbill.service.ThirdBillService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;

/**
 * @author swing
 * @date 2018年6月25日 下午2:59:25 类说明
 */
public class EmailBillServiceImpl implements EmailBillService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private ThirdBillService thirdBillservice = SpringBeanUtil.getBean(ThirdBillService.class);
	private String attachMentPath;
	private EmailConfig emailConfig;
	private Store store = null;
	private Folder folder = null;

	public EmailBillServiceImpl(String attachMentPath, EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
		this.attachMentPath = attachMentPath + "/" + emailConfig.getUserName() + "/";
	}

	/**
	 * 获取会话
	 * 
	 * @return
	 * @throws Exception
	 */
	private Session getSessionMail() throws Exception {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", emailConfig.getHost());
		properties.put("mail.smtp.auth", "true");
		Session sessionMail = Session.getDefaultInstance(properties, null);
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
		folder = store.getFolder("INBOX");
		// 设置只读
		folder.open(Folder.READ_ONLY);

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
	 * 根据邮件发送日期下载邮件附件
	 * 
	 * @param beginDate 邮件开始日期
	 * @param endDate   邮件结束日期
	 */
	private void downloadBill(String beginDate, String endDate) {
		Date begin = DateUtil.transferStringToDate("yyyy-MM-dd", beginDate);
		Date end = DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate + " 23:59:59");
		SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, begin);
		SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, end);
		SearchTerm sender = new FromStringTerm(emailConfig.getFrom());
		SearchTerm[] filter = { comparisonTermGe, comparisonTermLe, sender };
		SearchTerm comparisonAndTerm = new AndTerm(filter);
		try {
			folder = getFolder();
			if (folder != null) {
				Message[] messages = folder.search(comparisonAndTerm);
				if (messages.length > 0) {
					for (Message msg : messages) {
						doReceive(msg);
					}
				} else {
					if (log.isInfoEnabled()) {
						log.info("没有邮件接收");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnect();
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

	/**
	 * 处理接收到的邮件
	 * 
	 * @param msg
	 * @throws Exception
	 */
	private void doReceive(Message msg) throws Exception {
		String suject = getSubject(msg);
		String from = getFrom(msg);
		boolean isAttachMent = isContainAttach((Part) msg);
		log.info("接收到邮件,发送人:{},标题:{},发送时间:{}", from, suject,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((MimeMessage) msg).getSentDate()));
		if (isAttachMent) {
			log.info("开始下载附件");
			saveAttachMent(msg);
		} else {
			log.info("没有账单附件,不处理解析");
		}
		((POP3Message) msg).invalidate(true);
	}

	/**
	 * 保存邮件附件
	 * 
	 * @param part
	 * @throws Exception
	 */
	private void saveAttachMent(Part part) throws Exception {
		Message msg = (Message) part;
		Date sendDate = msg.getSentDate();
		// 因为邮件日期和账单日期相隔一天，这里要减去一天作为文件保存的目录（保证目录日期和账单日期相同）
		String billDate = DateUtil.getSpecifiedDayBefore(sendDate).replaceAll("-", "/");
		// 将发送人当做sourceType
		String sourceType = getFrom(msg).split("@")[1];
		String fileName = "";
		// 保存附件到服务器本地
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null)
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachMent(mpart);
				} else {
					fileName = mpart.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachMent((Part) part.getContent());
		}
	}

	/**
	 * 解压zip账单文件到当前目录
	 * 
	 * @param filePath
	 * @param sourceType
	 * @throws Exception
	 */
	private void decompress(String filePath, String sourceType) throws Exception {
		File file = new File(filePath);
		ZipFile zf = new ZipFile(file);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			if (!ze.isDirectory()) {
				long size = ze.getSize();
				if (size > 0) {
					String ext = ze.getName().substring(ze.getName().lastIndexOf("."));
					String fileName = emailConfig.getOrgCode() + "_" + sourceType + ext;
					InputStream inputStream = zf.getInputStream(ze);
					File billFile = new File(file.getParent(), fileName);
					OutputStream outputStream = new FileOutputStream(billFile);
					int len;
					byte[] buffer = new byte[8192];
					while (-1 != (len = inputStream.read(buffer))) {
						outputStream.write(buffer, 0, len);
					}
					outputStream.close();
					inputStream.close();
					try {
						this.doParseBill(billFile);
					} catch (Exception e) {
						log.error("解析账单异常:{}", e.getMessage());
					}
				}
			}
		}
		zin.closeEntry();
		zf.close();
		zin.close();
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 保存附件到指定目录里
	 * 
	 * @param fileName：附件名称
	 * @param in：文件输入流
	 * @param filePath：邮件附件存放基路径
	 */
	private void saveFile(String filePath, String fileName, String sourceType, InputStream in) throws Exception {
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
			this.decompress(file.getAbsolutePath(), sourceType);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void parseBill(String beginDate, String endDate) {
		this.downloadBill(beginDate, endDate);
	}

	private void doParseBill(File billFile) {
		// 解析账单
		List<ThirdBill> billList = readFileToList(billFile);
		// 账单入库
		saveBill(billList);
	}

	/**
	 * 根据文件解析账单
	 * 
	 * @param f
	 * @return
	 */
	private List<ThirdBill> readFileToList(File f) {
		List<ThirdBill> resultList = new ArrayList<>();
		AttachmentParser parseService = new PdfAttachmentParser(f);
		List<ThirdBill> list = parseService.convertToBean();
		resultList.addAll(list);
		return resultList;
	}

	private void saveBill(List<ThirdBill> billList) {
		if (billList.size() > 0) {
			ThirdBill bill = billList.get(0);
			String billDate = DateUtil.transferDateToString("yyyy-MM-dd", bill.getTradeDatatime());
			String beginTime = billDate + CommonConstant.START_TIME_FLAG;
			String endTime = billDate + CommonConstant.END_TIME_FLAG;
			List<String> orgCodes = new ArrayList<>();
			orgCodes.add(emailConfig.getOrgCode());
			// 先删除旧的数据
			thirdBillservice.delete(beginTime, endTime, orgCodes, "0149");
			thirdBillservice.batchInsertThirdBill(billList);
		}
	}
}
