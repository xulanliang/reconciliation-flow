package com.yiban.framework.core.api.oss.inface;

import java.io.IOException;
import java.io.InputStream;

import com.yiban.framework.core.api.oss.vo.UploadResult;

/**
 * 文件上传接口
 * @author swing
 *
 * @date 2016年11月30日 上午10:43:27
 */

public interface UploadService {

	UploadResult uploadNewFile(byte[] bytes) throws IOException;
	
	UploadResult uploadNewFile(byte[] bytes, String mineType) throws IOException;

	UploadResult updateOldFile(byte[] bytes, String ossFileName) throws IOException;

	UploadResult deleteFile(String ossFileName);

	/**
	 * 生成二维码图片并上传到oss
	 * 
	 * @param content
	 *            二维码内容
	 * @param width
	 *            二维码宽度
	 * @param height
	 *            二维码高度
	 * @param srcImagePath
	 *            二维码嵌入图片地址
	 * @param isUrl
	 *            图片地址是否为url地址， true url地址， false 本地文件
	 * @return
	 */
	UploadResult uploadQRcodeFile(String content, int width, int height, String srcImagePath, boolean isUrl)
			throws IOException;
}
