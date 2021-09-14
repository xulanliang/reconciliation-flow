package com.yiban.framework.core.service.samba;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SambaService {
	/**
	 * 上传图片到特定共享目录
	 * @param file  				文件流
	 * @param filename         文件名称
	 * @param filePath          文件存放的路径前缀
	 * @param user				用户名
	 * @param password        密码
	 * @param sambaIp         目标地址ip
	 * @param waterIconClassPath         图片在class resource的路径
	 * @return
	 */
	String uploadFile(InputStream file,String filename,String filePath,String user,String password,String sambaIp);
	/**
	 * 
	 * @param waterIconClassPath 水印图片路径
	 * @param file
	 * @param filename
	 * @param filePath
	 * @param user
	 * @param password
	 * @param sambaIp
	 * @return
	 */
	String uploadFile4WaterIcon(String  waterIconClassPath,InputStream file,String filename,String filePath,String user,String password,String sambaIp);
	/**
	 * 上传图片到特定共享目录
	 * @param file  				文件流
	 * @param filename         文件名称
	 * @param filePath          文件存放的路径前缀
	 * @param user				用户名
	 * @param password        密码
	 * @param sambaIp         目标地址ip
	 * @return
	 */
	OutputStream uploadFileDefault(String filename,String filePath,String user,String password,String sambaIp);
	/**
	 * 下载文件
	 * @param address      现在文件的路径地址
	 * @param user			用户名
	 * @param password   密码
	 * @param sambaIp     ip地址
	 * @param response
	 * @param request
	 */
	void getFile(String address,String user,String password,String sambaIp,HttpServletResponse response,HttpServletRequest request);	
	/**
	 * 获取文件流
	 * @param address      现在文件的路径地址
	 * @param user			用户名
	 * @param password   密码
	 * @param sambaIp     ip地址
	 * @param response
	 * @param request
	 */
	InputStream getFileDefault(String path,String user,String password,String sambaIp);	
	/**
	 * 下载文件
	 * @param attachmentFileName      下载是显示文件名eg:weixin.apk（可以不传）
	 * @param path      现在文件的路径地址
	 * @param user			用户名
	 * @param password   密码
	 * @param sambaIp     ip地址
	 * @param response
	 * @param request
	 */
	void normalGetFile(String attachmentFileName,String path,String user,String password,String sambaIp,HttpServletResponse response,HttpServletRequest request);
	void downFile(String attachmentFileName,String path,String user,String password,String sambaIp,HttpServletResponse response,HttpServletRequest request);
}
