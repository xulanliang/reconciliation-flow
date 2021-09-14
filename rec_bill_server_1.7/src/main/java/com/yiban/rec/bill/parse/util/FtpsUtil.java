package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

/**
 * ftps工具类
 * 
 * @author clearofchina
 *
 */
public class FtpsUtil {

	private final static Log logger = LogFactory.getLog(FtpsUtil.class);

	/**
	 * 获取FTPClient对象
	 *
	 * @param ftpsHost     FTPS主机服务器
	 * @param ftpsPassword FTPS 登录密码
	 * @param ftpsUserName FTPS登录用户名
	 * @param ftpsPort     FTPS端口 默认为21
	 * @return
	 */
	private static FTPSClient getFTPSClient(String ftpsHost, String ftpsUserName, String ftpsPassword, int ftpsPort) {
		FTPSClient ftpsClient = new FTPSClient();
		try {
			ftpsClient = new FTPSClient("TLS", true);
			// 连接FTP服务器
			ftpsClient.connect(ftpsHost, ftpsPort);
			// 登陆FTP服务器
			ftpsClient.login(ftpsUserName, ftpsPassword);
			ftpsClient.execPROT("P");
			ftpsClient.enterLocalPassiveMode();
			if (!FTPReply.isPositiveCompletion(ftpsClient.getReplyCode())) {
				logger.info("未连接到FTPS，用户名或密码错误。");
				ftpsClient.disconnect();
			} else {
				logger.info("FTPS连接成功。");
			}
		} catch (SocketException e) {
			logger.info("FTPS的IP地址可能错误，请正确配置。" + e);
		} catch (IOException e) {
			logger.info("FTPS的端口错误,请正确配置。" + e);
		}
		return ftpsClient;
	}

	/***
	 * 从FTPS服务器下载文件
	 * 
	 * @param ftpsHost     FTPS IP地址
	 * @param ftpsUserName FTPS 用户名
	 * @param ftpsPassword FTPS用户名密码
	 * @param ftpsPort     FTPS端口
	 * @param ftpsPath     FTPS服务器中文件所在路径 格式： ftpstest/aa
	 * @param localPath   下载到本地的位置 格式：H:/download
	 * @param fileName    文件名称
	 */
	public static File downloadFtpsFile(String ftpsHost, String ftpsUserName, String ftpsPassword, int ftpsPort,
			String ftpsPath, String localPath, String fileName) {

		FTPSClient ftpsClient = null;

		try {
			ftpsClient = getFTPSClient(ftpsHost, ftpsUserName, ftpsPassword, ftpsPort);
			// 中文支持
			ftpsClient.setControlEncoding("GBK");
			//ftpsClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpsClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpsClient.enterLocalPassiveMode();
			ftpsClient.changeWorkingDirectory(ftpsPath);

			File localFile = new File(localPath , fileName);
			if (!localFile.getParentFile().exists()) {
				localFile.getParentFile().mkdirs();
			}
			OutputStream os = new FileOutputStream(localFile);
			ftpsClient.retrieveFile(fileName, os);
			os.close();
			ftpsClient.logout();
			return localFile;
		} catch (FileNotFoundException e) {
			logger.error("没有找到" + ftpsPath + "文件，" + e);
		} catch (SocketException e) {
			logger.error("连接FTPS失败， " + e);
		} catch (IOException e) {
			logger.error("文件读取错误。" + e);
		}
		return null;
	}
	
	
	/***
	 * 从FTP服务器下载文件
	 * 
	 * @param ftpsHost     FTPS IP地址
	 * @param ftpsUserName FTPS 用户名
	 * @param ftpsPassword FTPS用户名密码
	 * @param ftpsPort     FTPS端口
	 * @param ftpsPath     FTPS服务器中文件所在路径 格式： ftpstest/aa
	 * @param localPath   下载到本地的位置 格式：H:/download
	 */
	public static  List<String> downloadFtpsFile(String ftpsHost, String ftpsUserName, String ftpsPassword, int ftpsPort,
			String ftpsPath, String localPath) {
		FTPSClient ftpsClient = null;
		//InputStream is = null;
        FileOutputStream fos = null;
        OutputStream is=null;
        //String[] returnFile =null;
        List<String> list = new ArrayList<String>();
        
        try {
			ftpsClient = getFTPSClient(ftpsHost, ftpsUserName, ftpsPassword, ftpsPort);
			// 中文支持
			ftpsClient.setControlEncoding("UTF-8");
			ftpsClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpsClient.enterLocalPassiveMode();
			//ftpClient.changeWorkingDirectory("/we_ali_pay_file/20180606");
			ftpsClient.changeWorkingDirectory(ftpsPath);
			ftpsClient.enterLocalPassiveMode();
			
			FTPFile[] fs = ftpsClient.listFiles(); 
			//System.out.println(fs.length);
            for (FTPFile ff : fs) { 
                //解决中文乱码问题，两次解码 
                //byte[] bytes=ff.getName().getBytes("iso-8859-1"); 
                //String fn=new String(bytes,"utf8");
                File localFile = new File(localPath +"\\"+ ff.getName());
                localFile.toString();
                list.add(localFile.toString());
                //System.out.println("文件创建路径为："+localFile);
                if (!localFile.getParentFile().exists()) {
    				localFile.getParentFile().mkdirs();
    			}
                 is = new FileOutputStream(localFile);
                ftpsClient.retrieveFile(ff.getName(), is);
               }
			ftpsClient.logout();
		} catch (Exception e) {
            logger.error("FTPS文件下载失败！", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return list;
	}
	

}
