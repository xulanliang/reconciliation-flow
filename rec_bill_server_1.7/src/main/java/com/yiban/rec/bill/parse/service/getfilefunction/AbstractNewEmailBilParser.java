package com.yiban.rec.bill.parse.service.getfilefunction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
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
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.util.Calendar;
import com.sun.mail.pop3.POP3Message;
import com.yiban.framework.utils.date.DateUtils;
import com.yiban.rec.bill.parse.util.ZipUtil;
import com.yiban.rec.bill.parse.vo.EmailConfig;

public class AbstractNewEmailBilParser {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	private String attachMentPath="D:\\billfile\\";
	
	private EmailConfig emailConfig;
	
	private Store store = null;
	private Folder folder = null;
	
	private String time="";
	public AbstractNewEmailBilParser(EmailConfig config){
		emailConfig=config;
		//保存本地地址
		attachMentPath="D:\\billfile\\";
		if( ! attachMentPath.endsWith(File.separator)){
			attachMentPath+=File.separator;
		}
		attachMentPath +=emailConfig.getUserName() + File.separator;
	}
	/**
	 * 获取会话
	 * 
	 * @return
	 * @throws Exception
	 */
	private Session getSessionMail() throws Exception {
		Properties properties = System.getProperties();
		properties.setProperty("mail.store.protocol", "pop3");       // 使用pop3协议  
		properties.setProperty("mail.pop3.port", String.valueOf(emailConfig.getPort()));           // 端口  
		properties.setProperty("mail.pop3.host", emailConfig.getHost());       // pop3服务器  
		properties.setProperty("mail.pop3.auth", "true");
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
		folder = store.getFolder("INBOX");
		// 设置只读
		folder.open(Folder.READ_WRITE);
		return folder;
	}
	
	/**
	 * 保存邮件附件
	 * 
	 * @param part
	 * @throws Exception
	 */
	private File saveAttachMent(Part part) throws Exception {
		File file=new File("");
		// 因为邮件日期和账单日期相隔一天，这里要减去一天作为文件保存的目录（保证目录日期和账单日期相同）
		String billDate = time.replaceAll("-", "/");
		// 将发送人当做sourceType
		String sourceType = emailConfig.getFrom().split("@")[1];
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
					if (StringUtils.isNotBlank(fileName)) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						file=saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				} else if (mpart.isMimeType("multipart/*")) {
					file=saveAttachMent(mpart);
				} else {
					fileName = mpart.getFileName();
					if (StringUtils.isNotBlank(fileName)) {
						fileName = MimeUtility.decodeText(fileName);
						String dir = attachMentPath + billDate;
						file=saveFile(dir, fileName, sourceType, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			file=saveAttachMent((Part) part.getContent());
		}
		return file;
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
						&& ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))){
					attachflag = true;
				}else if (mpart.isMimeType("multipart/*")) {
					attachflag = isContainAttach((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1){
						
						attachflag = true;
					}
					if (contype.toLowerCase().indexOf("name") != -1){
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
	 * 保存附件到指定目录里
	 * 
	 * @param fileName：附件名称
	 * @param in：文件输入流
	 * @param filePath：邮件附件存放基路径
	 */
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
			if(file.getName().endsWith(".zip")){
				return decompress(file.getAbsolutePath(), sourceType);
			}else{
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解压zip账单文件到当前目录
	 * @param filePath
	 * @param sourceType
	 * @throws Exception
	 */
	private File decompress(String filePath, String sourceType) throws Exception {
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
	 * 所有邮件类的账单都需要先通过电子邮箱下载账附件到本地
	 * 
	 * @return
	 */
	public File downloadAttachment(String date) {
		time=date;
		SearchTerm comparisonAndTerm = getSearch();
		try {
			folder = getFolder();
			if (folder != null) {
				Message[] messages = folder.search(comparisonAndTerm);
				if (messages.length > 0) {
					Message msg =messages[0];
					if (isContainAttach((Part) msg)) {
						logger.info("开始下载附件");
						File file= saveAttachMent(msg);
						((POP3Message) msg).invalidate(true);
						return file;
					} else {
						logger.info("没有账单附件,不处理解析");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnect();
		}
		return null;
	}
	
	protected SearchTerm getSearch(){
		Date begin = getStartDate();
		Date end = getEndDate();
		//邮件的开始时间
		SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, begin);
		//邮件的结束时间
		SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, end);
		//邮件主题（包含）
		//SearchTerm subjectTerm = new SubjectTerm(emailConfig.get);
		SearchTerm term = new FromStringTerm(emailConfig.getFrom());
		SearchTerm[] filter = { term,comparisonTermGe,comparisonTermLe};
		SearchTerm comparisonAndTerm = new AndTerm(filter);
		return comparisonAndTerm;
	}
	
	protected Date getStartDate(){
		Date begin = DateUtils.transferStringToDate("yyyy-MM-dd", time);
		Calendar ca = Calendar.getInstance();
		ca.setTime(begin);
		ca.add(Calendar.DAY_OF_MONTH, 1);
		return ca.getTime();
	}
	protected Date getEndDate(){
		Date end = DateUtils.transferStringToDate("yyyy-MM-dd HH:mm:ss", time + " 23:59:59");
		Calendar ca = Calendar.getInstance();
		ca.setTime(end);
		ca.add(Calendar.DAY_OF_MONTH, 1);
		return ca.getTime();
	}
	
	/*public void main(String[] args) {
		emailConfig=new EmailConfig("pop3.163.com", 110, "pop3", "xwj130066@163.com", "xwjtms130066", "xwj130066@163.com", "110",true,null);
		File file = downloadAttachment("2019-05-07");
		System.out.println(file.getName());
	} */
}
