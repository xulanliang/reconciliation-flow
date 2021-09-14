package com.yiban.rec.util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;

public class FileSaveUtil {

	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception { 
        File targetFile = new File(filePath);  
        if(!targetFile.exists()){    
            targetFile.mkdirs();    
        }       
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }
	
	public static boolean delAllFile(String path) throws Exception{
		boolean flag = false;
		try {
	        if(StringUtils.isBlank(path)){
	            return false;
	        }
	        File file = new File(path);
	        if(!file.exists())return flag;
	        String[] tempList= file.list();
	        File temp = null;
	        for (int i = 0; i < tempList.length; i++) {
	      	  if (path.endsWith(File.separator)) {
	      		  temp = new File(path + tempList[i]);
	      	  } else {
	      		  temp = new File(path + File.separator + tempList[i]);
	      	  }
	      	  if (temp.isFile()) {
	      		  temp.delete();
	      		  flag=true;
	      	  }
	      	  if (temp.isDirectory()) {
	      		  delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
	      		  flag = true;
	      	  }
		    }
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
      return flag;
   }
	
	public static boolean delFile(String path) throws Exception{
		boolean flag = false;
		try {
	        if(StringUtils.isBlank(path)){
	            return false;
	        }
	        File file = new File(path);
	        if(!file.exists())return flag;
	        file.delete();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
      return flag;
   }
}
