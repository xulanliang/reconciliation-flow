package com.yiban.rec.bill.parse.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	protected static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);
	
	/**
	 * 获取当前日期，不带时分秒
	 * 格式为：yyyy-MM-dd  
	 * @return 日期类型
	 */
	public static Date getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentDateStr = dateFormat.format(new Date());
		Date currentDate = null;
		try {
			currentDate = dateFormat.parse(currentDateStr);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return currentDate;
	}
	
	/**
	 * 获取当前日期，带时分秒
	 * 格式为：yyyy-MM-dd HH:mm:ss
	 * @return 日期类型
	 */
	public static Date getCurrentDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateStr = dateFormat.format(new Date());
		Date currentDate = null;
		try {
			currentDate = dateFormat.parse(currentDateStr);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return currentDate;
	}
	
	/**
	 * 获取当前日期，带时分秒
	 * 格式为：yyyy-MM-dd HH:mm:ss
	 * @return String类型
	 */
	public static String getCurrentTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTimeStr = dateFormat.format(new Date());
		return currentTimeStr;
	}
	
	/**
	 * 获取当前日期，带时分秒
	 * 格式为：yyyyMMddHH:mm:ss
	 * @return String类型
	 */
	public static String getCurrentTimeStringTo() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		String currentTimeStr = dateFormat.format(new Date());
		return currentTimeStr;
	}
	
	/**
	 * 获取当前日期，不带时分秒
	 * @return String类型
	 */
	public static String getCurrentDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentDateStr = dateFormat.format(new Date());
		return currentDateStr;
	}

	/**
	 * 把毫秒转化成日期
	 * 
	 * @param dateFormat(日期格式，例如：MM/
	 *            dd/yyyy HH:mm:ss)
	 * @param millSec(毫秒数)
	 * @return
	 */
	public static String transferLongToDate(String dateFormat, Long millSec) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = new Date(millSec);
		return sdf.format(date);
	}

	/**
	 * 把毫秒转化成日期
	 * 
	 * @param dateFormat(日期格式，例如：MM/
	 *            dd/yyyy HH:mm:ss)
	 * @param millSec(毫秒数)
	 * @return
	 * @throws ParseException
	 */
	public static String transferStringToDateFormat(String dateFormat, String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf2.format(sdf.parse(date));
	}
	
	public static String transferStringToDateFormat(String targetFormat,String sourceFormat, String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat sdf2 = new SimpleDateFormat(targetFormat);
		return sdf2.format(sdf.parse(date));
	}

	public static void main(String[] args) throws Exception {
		System.out.println(transferStringToDateFormat("yyyy-MM-dd","dd/MM/yyyy","19/09/2020"));
	}
	/**
	 * 把String转换日期
	 * 
	 * @param dateFormat(日期格式，例如：MM/
	 *            dd/yyyy HH:mm:ss)
	 * @param date
	 *            日期串
	 * @return
	 * @throws ParseException
	 */
	public static Date transferStringToDate(String dateFormat, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date2 = null;
		try {
			date2 = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date2;
	}
	
	/**
	 * "2019-02-25T09:36:41+08:00"
	 * @param value
	 * @return
	 * @throws ParseException 
	 */
	public static Date getDate(String time) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = formatter.parse(time);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDate=sdf.format(date);
		return sdf.parse(sDate);
	}
	
	
	public static String getNormalTime(long value) {    
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd") ;    
        String time = format.format(new Date(value)) ;    
        return time;    
    }  
	
	public static String getNormalFormatTime(String forMat,long value) {    
        SimpleDateFormat format = new SimpleDateFormat(forMat);    
        String time = format.format(new Date(value)) ;    
        return time;    
    }  
	
	public static Date transferStringToDateFormat(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(date);
	}

	public static String transferDateToDateFormat(String dateFormat, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String dateTr = null;
		try {
			dateTr = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTr;
	}
	

	/**
	 * 把Date转换String
	 * 
	 * @param dateFormat(日期格式，例如：MM/
	 *            dd/yyyy HH:mm:ss)
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static String transferDateToString(String dateFormat, Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	/**
	 * 获得指定日期的前N天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */
	public static String getNDayBefore(Date specifiedDay, int n) {
		Calendar c = Calendar.getInstance();

		c.setTime(specifiedDay);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - n);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
		return dayBefore;
	}

	/**
	 * 获得指定日期的前一天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */
	public static String getSpecifiedDayBefore(Date specifiedDay) {
		Calendar c = Calendar.getInstance();

		c.setTime(specifiedDay);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayBefore;
	}
	
	
	/**
	 * 获得指定日期的前一天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */
	public static String getSpecifiedDayBefore(String specifiedDay) {
		Calendar c = Calendar.getInstance();
		
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayBefore;
	}
	

	/**
	 * 获得指定日期的前n天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */
	public static String getSpecifiedDayBeforeDay(Date specifiedDay, int daySum) {
		Calendar c = Calendar.getInstance();

		c.setTime(specifiedDay);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - daySum);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayBefore;
	}

	/**
	 * 获得指定日期的后一天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static String getSpecifiedDayAfter(String specifiedDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}
	/**
	 * 获得指定日期的后N天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static String getSpecifiedDayAfter(String specifiedDay,Integer num) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + num);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}

	// 获取当天的开始时间
	public static java.util.Date getDayBegin() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	// 获取指定日期的后一天开始日期
	public static Date getBeginDayOfTomorrow(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	/**
	 * 获取指定不带时分秒的日期
	 * 格式为：yyyy-MM-dd  
	 * @return 日期类型
	 */
	public static Date getInputDateOnlyDay(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String inputDateStr = dateFormat.format(date);
		Date inputDate = null;
		try {
			inputDate = dateFormat.parse(inputDateStr);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return inputDate;
	}
	
	/**
	 * 获取指定日期的后N天，不带时分秒
	 * 格式为：yyyy-MM-dd  
	 * @return 日期类型
	 */
	public static Date getDateAfterInput(Date date,int day) {
		
		//获取时间
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, +day);
		
		//格式化时分秒
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String inputDateStr = dateFormat.format(cal.getTime());
		Date inputDate = null;
		try {
			inputDate = dateFormat.parse(inputDateStr);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return inputDate;
	}
	
	/**
	 * 比较时间大小
	 * 格式为：yyyy-MM-dd  
	 * @return 日期类型
	 */
	 public static int compareDate(String DATE1, String DATE2) {
		
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		 try {
			 Date dt1 = df.parse(DATE1);
			 Date dt2 = df.parse(DATE2);
			 if (dt1.getTime() > dt2.getTime()) {
				 return 1;
			 } else if (dt1.getTime() < dt2.getTime()) {
				 return -1;
			 } else {
				 return 0;
			 }
		 } catch (Exception exception) {
			 exception.printStackTrace();
		 }
		 return 0;
	}
	 
	 public static Date getBeginDayOfTomorrow(String time,String from) {
		 SimpleDateFormat df = new SimpleDateFormat(from);	
		 try {
			return df.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		 return null;
	}

	 /**
		 * 获取某日期区间的所有日期 日期倒序
		 *
		 * @param startDate  开始日期
		 * @param endDate    结束日期
		 * @param dateFormat 日期格式
		 * @return 区间内所有日期
		 */
		public static List<String> getPerDaysByStartAndEndDate(String startDate, String endDate, String dateFormat) {
			DateFormat format = new SimpleDateFormat(dateFormat);
			try {
				Date sDate = format.parse(startDate);
				Date eDate = format.parse(endDate);
				long start = sDate.getTime();
				long end = eDate.getTime();
				if (start > end) {
					return null;
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(eDate);
				List<String> res = new ArrayList<String>();
				while (end >= start) {
					res.add(format.format(calendar.getTime()));
					calendar.add(Calendar.DAY_OF_MONTH, -1);
					end = calendar.getTimeInMillis();
				}
				return res;
			} catch (ParseException e) {
				LOG.error(e.getMessage(), e);
			}

			return null;
		}
		
		/**
		 * 获取当前日期，自带日期格式
		 * @return String类型
		 */
		public static String getCurrentDateStringByFormat(String dateFormatStr) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
			String currentDateStr = dateFormat.format(new Date());
			return currentDateStr;
		}
}
