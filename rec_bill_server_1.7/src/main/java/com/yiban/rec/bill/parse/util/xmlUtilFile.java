package com.yiban.rec.bill.parse.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * @Author: 周柏臣
 * @Email: 18576653848@163.com
 * @Date: 2021/5/20 11:30
 * @Description:
 */
public class xmlUtilFile {
    public static void toFile(String xml,String path){
        try {
            File  file=new File(path);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if(!file .exists())
            {
                file.createNewFile();
            }
            Document doc = DocumentHelper.parseText(xml);
            OutputFormat format = new OutputFormat();
            format.setIndent(true);
            format.setNewlines(true);
            Writer fileWriter=new FileWriter(path);
            XMLWriter xmlWriter=new XMLWriter(fileWriter,format);
            xmlWriter.write(doc);
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}