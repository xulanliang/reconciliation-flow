package com.yiban.rec.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author swing
 * @date 2018年6月12日 上午9:02:54 类说明
 * 一个时间迭代器,可以连续输出开始日期到结束日期中间的每个日期
 */
public class DayIterator {
	private final Logger log =LoggerFactory.getLogger(this.getClass());
	private String benginDate;
	private String endDate;


	public String getBenginDate() {
		return benginDate;
	}

	public void setBenginDate(String benginDate) {
		this.benginDate = benginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public DayIterator(String benginDate, String endDate) {
		this.benginDate = benginDate;
		this.endDate = endDate;
	}

	/**
	 * 日期迭代处理逻辑
	 * @param callBack 回调接口
	 */
	public void next(NextDateProcess callBack) {
		int d = 0;
		while (true) {
			String date = DateUtil.getSpecifiedDayAfter(benginDate, d);
			d++;
			if(log.isInfoEnabled()){
				log.info("当前处理日期:{}",date);
			}
			callBack.process(date);
			if (date.equals(endDate)) {
				break;
			}
		}
	}

}
