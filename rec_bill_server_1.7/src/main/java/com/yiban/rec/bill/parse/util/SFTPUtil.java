package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {

	private final static Log log = LogFactory.getLog(SFTPUtil.class);

	private ChannelSftp sftp;
	private Session session;
	/** SFTP 登录用户名 */
	private String username;
	/** SFTP 登录密码 */
	private String password;
	/** 私钥 */
	private String privateKey;
	/** SFTP 服务器地址IP地址 */
	private String host;
	/** SFTP 端口 */
	private int port;

	/**
	 * 构造基于密码认证的sftp对象
	 */
	public SFTPUtil(String username, String password, String host, int port) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	/**
	 * 构造基于秘钥认证的sftp对象
	 */
	public SFTPUtil(String username, String host, int port, String privateKey) {
		this.username = username;
		this.host = host;
		this.port = port;
		this.privateKey = privateKey;
	}

	public SFTPUtil() {
	}

	/**
	 * 连接sftp服务器
	 */
	public void login() {
		try {
			JSch jsch = new JSch();
			if (privateKey != null) {
				jsch.addIdentity(privateKey);// 设置私钥
			}
			session = jsch.getSession(username, host, port);
			if (password != null) {
				session.setPassword(password);
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接 server
	 */
	public void logout() {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
			}
		}
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
			}
		}
	}

	/**
	 * 下载文件
	 *
	 * @param downloadFilePath
	 *            下载的文件完整目录
	 * @param saveFile
	 *            存在本地的路径
	 */
	public File download(String downloadFilePath, String saveFile) {
		try {
			int i = downloadFilePath.lastIndexOf('/');
			if (i == -1)
				return null;
			sftp.cd(downloadFilePath.substring(0, i));
			File file = new File(saveFile);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			sftp.get(downloadFilePath.substring(i + 1), fileOutputStream);
			fileOutputStream.close();
			return file;
		} catch (Exception e) {
			// LOG.error(e.getMessage());
			e.getMessage();
			return null;
		}
	}

	/*
	 **
	 * 下载文件
	 * 
	 * @param directory 下载目录
	 * 
	 * @param downloadFile 下载的文件
	 * 
	 * @param saveFile 存在本地的路径
	 */
	public File download(String directory, String downloadFile, String saveFile) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			//logger.info("#### filePath："+file);
			System.out.println("file:"+file.toString());
			/*
			 * if(!file.exists()){ file.mkdir(); } file.createNewFile();
			 */
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			sftp.get(downloadFile, fileOutputStream);
			fileOutputStream.close();
			return file;
		} catch (Exception e) {
			// LOG.error(e.getMessage());
			e.getMessage();
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * @param sftp
	 */
	public Vector<LsEntry> listFiles(String directory) throws SftpException {
		return sftp.ls(directory);
	}

	// 上传文件测试
	public static void main(String[] args) throws SftpException, IOException {
		SFTPUtil sftp = new SFTPUtil("shenzheneryuan", "k*#I;f{uK$2=tU8I", "114.80.247.51", 22);
		sftp.login();
		// sftp.download("/data/shenzheneryuan/alipay/",
		// "2088721459821834-2018-12-10.zip");
		// sftp.download(downloadFilePath, saveFile);
		// File file = sftp.download("/data/shenzheneryuan/alipay/",
		// "2088721459821834-2018-12-10.zip","d:\\PADoto");
		File file = sftp.download("/data/shenzheneryuan/alipay/", "2088721459821834-2018-12-11.zip",
				"E:\\2088721459821834-2018-12-11.zip");
		System.out.println(file);

		/*
		 * String directory = "/data/shenzheneryuan/alipay/"; Vector<LsEntry>
		 * files = null; //查看文件列表 try { files = sftp.listFiles(directory); }
		 * catch (SftpException e) { e.printStackTrace(); } for (LsEntry file :
		 * files) { System.out.println("###\t" + file.getFilename()); }
		 */

		sftp.logout();
	}

}
