package com.yiban.framework.core.service.samba.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yiban.framework.core.service.samba.SambaService;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Service
public class SambaServiceImpl implements SambaService {


	public static final String SAMBA_STRING= "smb://";
	@Override
	public String uploadFile(InputStream file, String filename,String filePath,String user,String password,String sambaIp) {
		InputStream in = null;
		OutputStream out = null;
		Calendar now = Calendar.getInstance();
		String year = String.valueOf(now.get(Calendar.YEAR));
		String month = String.valueOf(now.get(Calendar.MONTH)+1);
		String date = String.valueOf(now.get(Calendar.DATE));
		String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
		try {
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);

			SmbFile remoteFileDate = new SmbFile(sambaAddress+ filePath + year+"/"+month+"/"+date, auth);
			if (remoteFileDate.exists() && remoteFileDate.isDirectory()) {
			} else {
				remoteFileDate.mkdirs();
			}
			SmbFile remoteUploadFile = new SmbFile(sambaAddress + filePath +  year+"/"+month+"/"+date+"/" + filename, auth); 
			in = new BufferedInputStream(file);
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteUploadFile));
			byte[] buffer = new byte[1024];
			while ((in.read(buffer)) != -1) {
				out.write(buffer);
				buffer = new byte[1024];
			}
			return filePath + year+"/"+month+"/"+date+"/" + filename;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(out!=null)
				{
					out.close();
				}
				if(in!=null)
				{
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void getFile(String address, String user, String password,String sambaIp,HttpServletResponse response,HttpServletRequest request) {
        InputStream inputStream = null;   
        OutputStream out = null;   
        String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
        try {  
        	
        	NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);
			SmbFile smbFile = new SmbFile(sambaAddress+"/Resources/internal/"+address, auth);
			out = response.getOutputStream();
			inputStream = new BufferedInputStream(new SmbFileInputStream(smbFile));    
            long fSize = smbFile.length();  
	        // 下载  
	        response.setContentType("application/octet-stream");
			response.setHeader("Connection", "Keep-Alive");
	        response.setHeader("Accept-Ranges", "bytes");  
	        response.setHeader("Content-Length", String.valueOf(fSize));  
	        response.setHeader("Content-Disposition", "attachment; filename="+ smbFile.getName());  
	        long pos = 0;  
	        if (null != request.getHeader("Range")) {  
	            // 断点续传  
	            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);  
	            try {  
	                pos = Long.parseLong(request.getHeader("Range").replaceAll(  
	                        "bytes=", "").replaceAll("-", ""));  
	            } catch (NumberFormatException e) {  
	                pos = 0;  
	            }  
	        }    
	        String contentRange = new StringBuffer("bytes ").append(  
	                new Long(pos).toString()).append("-").append(  
	                new Long(fSize - 1).toString()).append("/").append(  
	                new Long(fSize).toString()).toString();  
	        response.setHeader("Content-Range", contentRange);  
	        inputStream.skip(pos);  
	        BufferedOutputStream bufferOut = new BufferedOutputStream(out);  
	        byte[] buffer = new byte[5 * 1024];  
	        int length = 0;  
	        while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {  
	            bufferOut.write(buffer, 0, length);  
	        }  
	        bufferOut.flush();  
	        bufferOut.close();     
        } catch (Exception e) {   
            e.printStackTrace();   
        }finally{   
            try {  
            	out.close();  
    	        inputStream.close();  
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }
	}
	@Override
	public void normalGetFile(String attachmentFileName,String address, String user, String password, String sambaIp,
			HttpServletResponse response, HttpServletRequest request) {
		 InputStream inputStream = null;   
	        OutputStream out = null;   
	        String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
	        try {  
	        	
	        	NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);
				SmbFile smbFile = new SmbFile(sambaAddress+address, auth);
				out = response.getOutputStream();
				inputStream = new BufferedInputStream(new SmbFileInputStream(smbFile));    
	            long fSize = smbFile.length();  
		        // 下载  
		        response.setContentType("application/octet-stream");
				response.setHeader("Connection", "Keep-Alive");
		        response.setHeader("Accept-Ranges", "bytes");  
		        response.setHeader("Content-Length", String.valueOf(fSize));  
		        if(StringUtils.isBlank(attachmentFileName))
		        {
		        	attachmentFileName = smbFile.getName();
		        }
		        response.setHeader("Content-Disposition", "attachment; filename="+ attachmentFileName);  
		        long pos = 0;  
		        if (null != request.getHeader("Range")) {  
		            // 断点续传  
		            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);  
		            try {  
		                pos = Long.parseLong(request.getHeader("Range").replaceAll(  
		                        "bytes=", "").replaceAll("-", ""));  
		            } catch (NumberFormatException e) {  
		                pos = 0;  
		            }  
		        }    
		        String contentRange = new StringBuffer("bytes ").append(  
		                new Long(pos).toString()).append("-").append(  
		                new Long(fSize - 1).toString()).append("/").append(  
		                new Long(fSize).toString()).toString();  
		        response.setHeader("Content-Range", contentRange);  
		        inputStream.skip(pos);  
		        BufferedOutputStream bufferOut = new BufferedOutputStream(out);  
		        byte[] buffer = new byte[5 * 1024];  
		        int length = 0;  
		        while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {  
		            bufferOut.write(buffer, 0, length);  
		        }  
		        bufferOut.flush();  
		        bufferOut.close();     
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        }finally{   
	            try {  
	            	if(out!=null)
	            	{
	            		out.close();  
	            	}
	            	if(inputStream!=null)
	            	{
	            		inputStream.close();  
	            	}
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }
		
	}

	@Override
	public OutputStream uploadFileDefault( String filename, String filePath, String user, String password,
			String sambaIp) {
		InputStream in = null;
		OutputStream out = null;
		String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
		try {
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);

			SmbFile remoteFileDate = new SmbFile(sambaAddress+ filePath, auth);
			if (remoteFileDate.exists() && remoteFileDate.isDirectory()) {
			} else {
				remoteFileDate.mkdirs();
			}
			SmbFile remoteUploadFile = new SmbFile(sambaAddress + filePath + filename, auth); 
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteUploadFile));
			return out;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in!=null)
				{
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public InputStream getFileDefault(String path, String user, String password, String sambaIp) {

		 InputStream inputStream = null;   
	        String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
	        try {  
	        	
	        	NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);
				SmbFile smbFile = new SmbFile(sambaAddress+path, auth);
				inputStream = new BufferedInputStream(new SmbFileInputStream(smbFile));    
	        }
	    	catch(Exception e){
			}
	        return inputStream;
	}

	@Override
	public void downFile(String attachmentFileName, String address, String user, String password, String sambaIp,
			HttpServletResponse response, HttpServletRequest request) {
		 InputStream inputStream = null;   
	        OutputStream out = null;   
	        String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
	        try {  
	        	
	        	NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);
				SmbFile smbFile = new SmbFile(sambaAddress+address, auth);
				out = response.getOutputStream();
				inputStream = new BufferedInputStream(new SmbFileInputStream(smbFile));    
	            long fSize = smbFile.length();  
		        // 下载  
				response.setHeader("Connection", "Keep-Alive");
		        response.setHeader("Accept-Ranges", "bytes");  
		        response.setHeader("Content-Length", String.valueOf(fSize));  
		        if(StringUtils.isBlank(attachmentFileName))
		        {
		        	attachmentFileName = smbFile.getName();
		        }
		        response.setHeader("Content-Disposition", "attachment; filename="+ attachmentFileName);  
		        long pos = 0;  
		        if (null != request.getHeader("Range")) {  
		            // 断点续传  
		            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);  
		            try {  
		                pos = Long.parseLong(request.getHeader("Range").replaceAll(  
		                        "bytes=", "").replaceAll("-", ""));  
		            } catch (NumberFormatException e) {  
		                pos = 0;  
		            }  
		        }    
		        String contentRange = new StringBuffer("bytes ").append(  
		                new Long(pos).toString()).append("-").append(  
		                new Long(fSize - 1).toString()).append("/").append(  
		                new Long(fSize).toString()).toString();  
		        response.setHeader("Content-Range", contentRange);  
		        inputStream.skip(pos);  
		        BufferedOutputStream bufferOut = new BufferedOutputStream(out);  
		        byte[] buffer = new byte[5 * 1024];  
		        int length = 0;  
		        while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {  
		            bufferOut.write(buffer, 0, length);  
		        }  
		        bufferOut.flush();  
		        bufferOut.close();     
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        }finally{   
	            try {  
	            	if(out!=null)
	            	{
	            		out.close();  
	            	}
	            	if(inputStream!=null)
	            	{
	            		inputStream.close();  
	            	}
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }
	}

	@Override
	public String uploadFile4WaterIcon(String  waterIconFile,InputStream file, String filename, String filePath, String user, String password,
			String sambaIp) {
		InputStream in = null;
		OutputStream out = null;
		Calendar now = Calendar.getInstance();
		String year = String.valueOf(now.get(Calendar.YEAR));
		String month = String.valueOf(now.get(Calendar.MONTH)+1);
		String date = String.valueOf(now.get(Calendar.DATE));
		String sambaAddress = SAMBA_STRING+user+":"+password +"@"+sambaIp;
		try {
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(sambaIp, user, password);

			SmbFile remoteFileDate = new SmbFile(sambaAddress+ filePath + year+"/"+month+"/"+date, auth);
			if (remoteFileDate.exists() && remoteFileDate.isDirectory()) {
			} else {
				remoteFileDate.mkdirs();
			}
			SmbFile remoteUploadFile = new SmbFile(sambaAddress + filePath +  year+"/"+month+"/"+date+"/" + filename, auth); 
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteUploadFile));
			Thumbnails.of(file)   
			        .size(1280, 1024)  
			        .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(SambaServiceImpl.class.getResourceAsStream(waterIconFile)), 0.9f).toOutputStream(out);
			return filePath + year+"/"+month+"/"+date+"/" + filename;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(out!=null)
				{
					out.close();
				}
				if(in!=null)
				{
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}	
}
