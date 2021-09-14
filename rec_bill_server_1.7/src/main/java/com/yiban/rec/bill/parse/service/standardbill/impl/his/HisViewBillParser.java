package com.yiban.rec.bill.parse.service.standardbill.impl.his;


import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.ProConfigManager;

public abstract class HisViewBillParser<T> extends  AbstractHisBillParser<T> {
	public JdbcTemplate jdbcTemplate;
	
	public String  startTime="";
	public String  endTime="";
	public String  orgNo="";

	public JdbcTemplate getConnect() {
		String driverName = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisDriverName);
		String url = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisAdd);
		String username = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisUser);
		String password = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisPass);
		logger.info("连接his视图的数据源信息：driverName = " + driverName + ", url = " + url + ", username = " + username
				+ ", password = " + password);
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setDriverClassName(driverName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return jdbcTemplate=new JdbcTemplate(dataSource);
	}
	
	public List<T> getHisList(String sTime,String eTime,String orgCode) throws BillParseException{
		startTime=sTime + " 00:00:00";
		endTime=eTime+" 23:59:59";
		orgNo=orgCode;
		getConnect();
		return getList(jdbcTemplate);
	}
	
	public abstract List<T> getList(JdbcTemplate jdbcTemplate) throws BillParseException;
}
