package com.yiban.rec.netty.common;

/**
 * 
 * @ClassName: GlobalConstValue
 * @Description: 服务器常量定义
 * @author chuntu tuchun168@163.com
 * @date 2016年5月3日 上午11:25:42
 *
 */
public class GlobalConstValue {

	/**
	 * 帧头的长度
	 **/
	public static final int FRM_HEAD_LENGTH = 2;


	/**
	 * 数据长度
	 */
	public static final int FRM_DATA_LENGTH = 4;

	/**
	 * 命令的长度
	 **/
	public static final int FRM_CMD_LENGTH = 2;



	/**
	 * 基本帧长度
	 */
	public static final int FRM_BASIC_LENGTH = FRM_DATA_LENGTH;


	public static final String HEAD="*Q";
	/**
	 * 基本数据类型长度
	 */
	public final static int BYTE_LEN = 1;

	public final static int SHORT_LEN = 2;

	public final static int INT_LEN = 4;

	public final static int LONG_LEN = 8;

	public final static int FLOAT_LEN = 4;

	public final static int DOUBLE_LEN = 8;

	public final static int DATE_TIME = 8;

	public final static int CHAR_LEN = 1;

}
