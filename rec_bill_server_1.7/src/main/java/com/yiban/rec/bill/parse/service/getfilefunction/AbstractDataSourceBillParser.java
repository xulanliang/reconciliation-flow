package com.yiban.rec.bill.parse.service.getfilefunction;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;

public abstract class AbstractDataSourceBillParser<T> extends AbstractBillParser<T> {

	private DriverManagerDataSource getDataSource(String jdbcUrl, String driverClassName, String username,
			String password) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	/**
	 * 获取数据
	 * 
	 * @param jdbcUrl         jdbc:oracle:thin:@localhost:1521:orcl
	 * @param driverClassName 驱动 oracle.jdbc.OracleDriver
	 * @param username        用户名
	 * @param password        密码
	 * @param sql
	 * @param rowMapper
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<T> query(String jdbcUrl, String driverClassName, String username, String password, String sql,
			RowMapper rowMapper, Object... args) {
		logger.info("连接数据库信息：jdbcUrl = " + jdbcUrl);
		logger.info(" username = " + username);
		logger.info(" password = " + password);
		logger.info(" driverClassName = " + driverClassName);
		logger.info(" sql = " + sql);
		if (args != null && args.length > 0) {
			for (Object obj : args) {
				logger.info("参数：" + obj);
			}
		}
		JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(jdbcUrl, driverClassName, username, password));
		return jdbcTemplate.query(sql, rowMapper, args);
	}
}
