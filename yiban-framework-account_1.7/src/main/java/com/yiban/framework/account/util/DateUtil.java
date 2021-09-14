package com.yiban.framework.account.util;

import com.yiban.framework.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    protected static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_SHORT_FORMAT = "yyyy-MM-dd";
    public static final String PRIVATE_SHORT_FORMAT = "yyyy/MM/dd";
    public static final String STANDARD_LONG_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String MILLISECOND_LONG_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddhhmmss = "yyyyMMDDhhmmss";
    public static final String HHmmss = "HH:mm:ss";

    public static boolean isDate(String str) {
        //yyyy-MM-dd
        //String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))";
        // 加了时间验证的YYYY-MM-DD 00:00:00
        String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
        //yyyyMMddHHmmss
        //String regex = "^((((1[6-9]|[2-9]\\d)\\d{2})(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})(0?[13456789]|1[012])(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))(20|21|22|23|[0-1]?\\d)[0-5]?\\d[0-5]?\\d$";
        return str.matches(regex);
    }

    /**
     * 获取当前日期，不带时分秒
     * 格式为：yyyy-MM-dd
     *
     * @return 日期类型
     */
    public static Date getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(currentDateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return currentDate;
    }

    /**
     * 获取当前日期，带时分秒
     * 格式为：yyyy-MM-dd HH:mm:ss
     *
     * @return 日期类型
     */
    public static Date getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = dateFormat.format(new Date());
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(currentDateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return currentDate;
    }

    /**
     * 获取当前日期，带时分秒
     * 格式为：yyyy-MM-dd HH:mm:ss
     *
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
     *
     * @return String类型
     */
    public static String getCurrentTimeStringTo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        String currentTimeStr = dateFormat.format(new Date());
        return currentTimeStr;
    }

    /**
     * 获取当前日期，不带时分秒
     *
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
     * @param dateFormat(日期格式，例如：MM/ dd/yyyy HH:mm:ss)
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
     * @param dateFormat(日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @param millSec(毫秒数)
     * @return
     * @throws ParseException
     */
    public static String transferStringToDateFormat(String dateFormat, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat);
        try {
            return sdf2.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatStringDate(String dateFormat, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String transToFormat(String sourceFormat, String targetFormat, String date) {
        SimpleDateFormat sourceSdf = new SimpleDateFormat(sourceFormat);
        SimpleDateFormat targetSdf = new SimpleDateFormat(targetFormat);

        try {
            return targetSdf.format(sourceSdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 把String转换日期
     *
     * @param dateFormat(日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @param date                   日期串
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

    public static String getNormalTime(long value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(new Date(value));
        return time;
    }

    public static String getNormalFormatTime(String forMat, long value) {
        SimpleDateFormat format = new SimpleDateFormat(forMat);
        String time = format.format(new Date(value));
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
     * @param dateFormat(日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @param date
     * @return
     * @throws ParseException
     */
    public static String transferDateToString(String dateFormat, Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
     * 获得前N天的开始时间
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getNDayOfBeforeStart(Date specifiedDay, int n) {
        Calendar c = Calendar.getInstance();

        c.setTime(specifiedDay);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - n);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()) + " 00:00:00";
        return dayBefore;
    }

    /**
     * 获得前N天的结束时间
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getNDayOfBeforeEnd(Date specifiedDay, int n) {
        Calendar c = Calendar.getInstance();

        c.setTime(specifiedDay);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - n);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()) + " 23:59:59";
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
     * 获得指定时间的后几分钟
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDateTimeAfter(Date specifiedDay, int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(specifiedDay);
        int minute = c.get(Calendar.MINUTE);
        c.set(Calendar.MINUTE, minute + minutes);
        String dateTimeAfter = new SimpleDateFormat(STANDARD_FORMAT).format(c.getTime());
        return dateTimeAfter;
    }

    /**
     * 获取之情时间的前一个月
     *
     * @param formatter
     * @param date
     * @return
     */
    public static String getLastMonth(String formatter, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatter);
        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
        date = calendar.getTime();
        String accDate = format.format(date);
        return accDate;
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
     * 获得指定日期的前n月
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBeforeMonth(Date specifiedDay, int monthSum) {
        Calendar c = Calendar.getInstance();
        c.setTime(specifiedDay);
        c.add(Calendar.MONTH, -monthSum);

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
    public static String getSpecifiedDayAfter(String specifiedDay, Integer num) {
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
    public static Date getDayBegin() {
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
     *
     * @return 日期类型
     */
    public static Date getInputDateOnlyDay(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inputDateStr = dateFormat.format(date);
        Date inputDate = null;
        try {
            inputDate = dateFormat.parse(inputDateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return inputDate;
    }

    /**
     * 获取指定日期的后N天，不带时分秒
     * 格式为：yyyy-MM-dd
     *
     * @return 日期类型
     */
    public static Date getDateAfterInput(Date date, int day) {

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
            logger.error(e.getMessage());
        }
        return inputDate;
    }

    /**
     * 比较时间大小
     *
     * @return 日期类型
     */
    public static int compareDate(String date1, String date2, String format) {

        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
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


    /**
     * 获取当前字符类型时间，格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentMillisecondString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MILLISECOND_LONG_FORMAT);
        String currentTimeStr = dateFormat.format(new Date());
        return currentTimeStr;
    }


    /**
     * String转Date yyyy-MM-dd
     *
     * @return
     */
    public static Date getStringToDateTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(STANDARD_SHORT_FORMAT);
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(time);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return currentDate;
    }

    /**
     * Date转String yyyy/MM/dd
     *
     * @return
     */
    public static String stringToDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(PRIVATE_SHORT_FORMAT);
        String currentTimeStr = dateFormat.format(date);
        return currentTimeStr;
    }

    /**
     * Date转String yyyy-MM-dd
     *
     * @return
     */
    public static String getDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(STANDARD_SHORT_FORMAT);
        String currentTimeStr = dateFormat.format(date);
        return currentTimeStr;
    }

    /**
     * 获取指定日期是星期几：参数为null时表示获取当前日期是星期几
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * 获取指定日期一天之后的日期
     *
     * @param date
     * @return
     */
    public static Date nextDay(Date date) {
        return addDay(Calendar.DAY_OF_MONTH, date, 1);
    }

    /**
     * 获取指定天数之后的日期
     *
     * @param date
     * @param hav
     * @return
     */
    public static Date addDay(Date date, int hav) {
        return addDay(Calendar.DAY_OF_MONTH, date, hav);
    }

    /**
     * 获取指定分钟之后的日期
     *
     * @param date
     * @param hav
     * @return
     */
    public static Date addSeconds(Date date, int senconds) {
        return addDay(Calendar.SECOND, date, senconds);
    }

    /**
     * 获取指定分钟之后的日期
     *
     * @param date
     * @param hav
     * @return
     */
    public static Date addMinute(Date date, int minutes) {
        return addDay(Calendar.MINUTE, date, minutes);
    }

    /**
     * 获取指定天数之后的日期时间戳
     *
     * @param dateType
     * @param date
     * @param hav
     * @return
     */
    public static Date addDay(int dateType, Date date, int hav) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(dateType, hav);
        return cal.getTime();
    }

    /**
     * 字符转日期，格式: yyyy-MM-dd HH:mm:ss
     *
     * @param datetimestr
     * @return
     */
    public static Date stringLineToDateTime(String datetimestr) {
        if (StringUtil.isEmpty(datetimestr)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(STANDARD_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(datetimestr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return date;
    }

    /**
     * 字符转日期，格式: yyyy-MM-dd HH:mm:ss
     *
     * @param datetimestr
     * @return
     */
    public static Date stringLineToDateTime(String datetimestr, String format) {
        if (StringUtil.isEmpty(datetimestr)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dateFormat.parse(datetimestr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return date;
    }

    /**
     * 字符转日期，格式：yyyy-MM-dd
     *
     * @param datetimestr
     * @return
     */
    public static Date stringLineToDate(String datetimestr) {
        if (StringUtil.isEmpty(datetimestr)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(STANDARD_SHORT_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(datetimestr);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return date;
    }

    /**
     * 日期转字符，格式: yyyy-MM-dd HH:mm:ss
     *
     * @param datetimestr
     * @return
     */
    public static String dateTimeToStringLine(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(STANDARD_FORMAT);
        String datetimeStr = dateFormat.format(date);
        return datetimeStr;
    }

    /**
     * 日期转字符格式，可指定格式
     *
     * @param datetimestr
     * @return
     */
    public static String dateTimeToStringLine(Date date, String dataForm) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dataForm);
            String datetimeStr = dateFormat.format(date);
            return datetimeStr;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int daysBetween2Date(Date beginDate, Date endDate) {
        try {
            long from = beginDate.getTime();
            long to = endDate.getTime();
            int days = (int) ((to - from) / (1000 * 60 * 60 * 24));
            return days;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 计算两个时间之间的分钟数
     *
     * @param newTime
     * @param oldTime
     * @return
     */
    public static long minuesBetween2DateTime(String newTime, String oldTime) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(STANDARD_FORMAT);
            long NTime = df.parse(newTime).getTime();
            //从对象中拿到时间
            long OTime = df.parse(oldTime).getTime();
            long diff = (NTime - OTime) / 1000 / 60;
            return diff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static boolean isRightFormat(String date) {
        Date d = transferStringToDate(yyyyMMddHHmmss, date);
        if (null != d) {
            return true;
        }
        return false;
    }

    public static boolean isRightFormat(String date, String dateformate) {
        Date d = transferStringToDate(dateformate, date);
        if (null != d) {
            return true;
        }
        return false;
    }

    public static String nowAfterSeconds(int seconds) {
        try {
            Date now = getCurrentDateTime();
            now = addSeconds(now, seconds);
            return dateTimeToStringLine(now, yyyyMMddHHmmss);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取随机时间
     *
     * @param beginDate
     * @param endDate
     * @return Date
     */
    public static Date randomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(STANDARD_FORMAT);
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

    /**
     * 把日期转换为相应格式
     *
     * @param fmt
     * @param date
     * @return
     */
    public static Date transferDateToDate(String fmt, Date date) {
        String fmtDate = transferDateToDateFormat(fmt, date);
        return transferStringToDate(fmt, fmtDate);
    }
}
