package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.yiban.rec.bill.parse.vo.FTPConfig;

/**
 * ftp工具类
 * 
 * @author clearofchina
 *
 */
public class FtpUtil {

	private final static Log logger = LogFactory.getLog(FtpUtil.class);

	/**
	 * 获取FTPClient对象
	 *
	 * @param ftpHost     FTP主机服务器
	 * @param ftpPassword FTP 登录密码
	 * @param ftpUserName FTP登录用户名
	 * @param ftpPort     FTP端口 默认为21
	 * @return
	 */
	private static FTPClient getFTPClient(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort) {
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient = new FTPClient();
			// 连接FTP服务器
			ftpClient.connect(ftpHost, ftpPort);
			// 登陆FTP服务器
			ftpClient.login(ftpUserName, ftpPassword);
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				logger.info("未连接到FTP，用户名或密码错误。");
				ftpClient.disconnect();
			} else {
				logger.info("FTP连接成功。");
			}
		} catch (SocketException e) {
			logger.info("FTP的IP地址可能错误，请正确配置。" + e);
		} catch (IOException e) {
			logger.info("FTP的端口错误,请正确配置。" + e);
		}
		return ftpClient;
	}
	
	private static ChannelSftp  getSFTPClient(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort) {
		 ChannelSftp sftp = null;
	        try {
	            JSch jsch = new JSch();
	            jsch.getSession(ftpUserName, ftpHost, ftpPort);
	            Session sshSession = jsch.getSession(ftpUserName, ftpHost, ftpPort);
	            sshSession.setPassword(ftpPassword);
	            Properties sshConfig = new Properties();
	            sshConfig.put("StrictHostKeyChecking", "no");
	            sshSession.setConfig(sshConfig);
	            sshSession.connect();
	            logger.info("SFTP Session connected.");
	            Channel channel = sshSession.openChannel("sftp");
	            channel.connect();
	            sftp = (ChannelSftp) channel;
	            logger.info("Connected to " + ftpHost);
	        } catch (Exception e) {
	            logger.error(e.getMessage());
	        }
	        return sftp;
	}

	/***
	 * 从FTP服务器下载文件
	 * 
	 * @param ftpHost     FTP IP地址
	 * @param ftpUserName FTP 用户名
	 * @param ftpPassword FTP用户名密码
	 * @param ftpPort     FTP端口
	 * @param ftpPath     FTP服务器中文件所在路径 格式： ftptest/aa
	 * @param localPath   下载到本地的位置 格式：H:/download
	 * @param fileName    文件名称
	 */
	public static File downloadFtpFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort,
			String ftpPath, String localPath, String fileName) {

		FTPClient ftpClient = null;

		try {
			ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
			logger.info("login ftp server success，get ftpClient object success：" + ftpClient.toString());
			// 中文支持
			ftpClient.setControlEncoding("GBK");
			//ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			//这个设置允许被动连接--访问远程ftp时需要
			ftpClient.enterLocalPassiveMode();
//			ftpClient.enterLocalActiveMode();
			FTPClientConfig config = new FTPClientConfig();
			config.setLenientFutureDates(true);
			ftpClient.configure(config);

			ftpClient.changeWorkingDirectory(ftpPath);
//			ftpClient.enterLocalActiveMode();
			FTPFile[] list = ftpClient.listFiles();
			logger.info("ftp文件列表大小: " + list.length);
//			System.out.println(432);
			List<FTPFile> files = Arrays.stream(ftpClient.listFiles()).collect(Collectors.toList());
			for (int i = 0; i < files.size() - 1; i++) {
				logger.info("ftp文件：" + files.get(i).getName());
				logger.info("ftp文件Size：" + files.get(i).getName());
			}

			File localFile = new File(localPath , fileName);
			if (!localFile.getParentFile().exists()) {
				localFile.getParentFile().mkdirs();
			}
			OutputStream os = new FileOutputStream(localFile);
//			System.out.println("download file(ftpPath+fileName)======"+ ftpPath + "/" + localFile.getName());
			logger.info("download file(ftpPath+fileName)======"+ ftpPath + "/" + localFile.getName());
//			System.out.println("fileName:"+fileName );
//			System.out.println("list[0].getName():"+list[0].getName() );
			ftpClient.retrieveFile(fileName, os);
			//ftpClient.retrieveFile(list[1].getName(), os);
			//ftpClient.retrieveFileStream(new String(ftpPath.getBytes("GBK"),"ISO-8859-1"));
			os.close();
			ftpClient.logout();
			return localFile;
		} catch (FileNotFoundException e) {
			logger.error("没有找到" + ftpPath + "文件，" + e);
		} catch (SocketException e) {
			logger.error("连接FTP失败， " + e);
		} catch (IOException e) {
			logger.error("文件读取错误。" + e);
		}
		return null;
	}
	
	public static File downloadSftpFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort,
			String ftpPath, String localPath, String fileName) {
		ChannelSftp sftp=null;
		try {
			sftp=getSFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            sftp.cd(ftpPath);
            File file = new File(localPath,fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            sftp.get(fileName, fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
	}
	
	/***
	 * 从FTP服务器下载文件
	 * 
	 * @param ftpHost     FTP IP地址
	 * @param ftpUserName FTP 用户名
	 * @param ftpPassword FTP用户名密码
	 * @param ftpPort     FTP端口
	 * @param ftpPath     FTP服务器中文件所在路径 格式： ftptest/aa
	 * @param localPath   下载到本地的位置 格式：H:/download
	 */
	public static  List<String> downloadFtpFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort,
			String ftpPath, String localPath) {
		FTPClient ftpClient = null;
		//InputStream is = null;
        FileOutputStream fos = null;
        OutputStream is=null;
        //String[] returnFile =null;
        List<String> list = new ArrayList<String>();
        
        try {
			ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
			// 中文支持
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			//ftpClient.changeWorkingDirectory("/we_ali_pay_file/20180606");
			ftpClient.changeWorkingDirectory(ftpPath);
			ftpClient.enterLocalPassiveMode();
			
			FTPFile[] fs = ftpClient.listFiles(); 
			//System.out.println(fs.length);
            for (FTPFile ff : fs) { 
            	System.out.println(ff.getName());
                //解决中文乱码问题，两次解码 
                //byte[] bytes=ff.getName().getBytes("iso-8859-1"); 
                //String fn=new String(bytes,"utf8");
                File localFile = new File(localPath +"\\"+ ff.getName());
                localFile.toString();
                list.add(localFile.toString());
                System.out.println("文件创建路径为："+localFile);
                if (!localFile.getParentFile().exists()) {
    				localFile.getParentFile().mkdirs();
    			}
                 is = new FileOutputStream(localFile);
                ftpClient.retrieveFile(ff.getName(), is);
               }
			ftpClient.logout();
		} catch (Exception e) {
            logger.error("FTP文件下载失败！", e);
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
	
	/**
	 * 下载文件
	 * @param config ftp相关参数
	 * @param isDownAll 是否下载目录下所有文件
	 * @param ftpName 需要下载的文件名
	 * @param fileName 需要存的文件名
	 * @return
	 */
	public static List<File> downloadFtpFile(FTPConfig config, boolean isDownAll, String ftpName, String fileName) {
		List<File> fileList = new ArrayList<File>();
		FTPClient ftpClient = null;
        FileOutputStream os = null;
		try {
			ftpClient = getFTPClient(config.getHost(), 
										config.getUsername(), 
										config.getPassword(), 
										config.getPort());
			logger.info("连接FTP成功！");
			ftpClient.setControlEncoding("GBK");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalActiveMode();
			FTPClientConfig FTPClientConfig = new FTPClientConfig();
			FTPClientConfig.setLenientFutureDates(true);
			ftpClient.configure(FTPClientConfig);
			ftpClient.changeWorkingDirectory(config.getFtpPath());
			ftpClient.enterLocalActiveMode();
			File localFile = new File(config.getSavePath());
			if (!localFile.exists()) {
				localFile.mkdirs();
			}
			//下载指定文件
			if(!isDownAll) {
				localFile = new File(config.getSavePath() + fileName);
				os = new FileOutputStream(localFile);
				ftpClient.retrieveFile(ftpName, os);
				fileList.add(localFile);
				return fileList;
			}
			
			for (FTPFile ftp : ftpClient.listFiles()) {
				ftpName = ftp.getName();
				localFile = new File(config.getSavePath() + fileName + ftpName.substring(8));
				os = new FileOutputStream(localFile);
				ftpClient.retrieveFile(ftpName, os);
				fileList.add(localFile);
			}
			os.close();
			ftpClient.logout();
			return fileList;
		}  catch (Exception e) {
			logger.error("文件读取错误。" + e);
		}
		return null;
	}
	

}
