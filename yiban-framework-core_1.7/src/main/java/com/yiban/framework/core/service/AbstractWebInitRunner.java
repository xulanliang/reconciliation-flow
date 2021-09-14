package com.yiban.framework.core.service;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.yiban.framework.core.util.SpringBeanUtil;

/**
 * @author swing
 * @date 2018年1月10日 下午3:48:54 类说明 WebInitTask 接口抽象实现
 */
public abstract class AbstractWebInitRunner implements CommandLineRunner {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public final void run(String... args) throws Exception {
		init();
		excute();
		finish();
	}

	public abstract void excute() throws Exception;

	private final void init() {
//		logger.info("==初始化任务开始==");
	}

	private final void finish() {
//		logger.info("==初始化任务结束==");
	}

	/**
	 * 读取classpath conf/sql目录下的脚本文件，并执行
	 * @param fileName
	 * @param charset
	 */
	protected final void excuteSqlByName(String fileName, String charset) {
		final Resource resource = new ClassPathResource("conf/sql/" + fileName);
		if (resource.exists()) {
			try {
				List<String> lines = IOUtils.readLines(resource.getInputStream(), Charset.forName(charset));
				StringBuilder sb = new StringBuilder(300);
				for (String line : lines) {
					if (line.endsWith(";")) {
						line = line + ";";
					}
					sb.append(line);
				}
				if (sb.length() > 0) {
					excuteSql(sb.toString());
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected final boolean isExistsRecord(String tableName) throws Exception {
		if (StringUtils.isEmpty(tableName)) {
			throw new RuntimeException("数据表不能为空");
		}
		final String sql = "select count(0)total from " + tableName + " limit 1";
		DataSource db = SpringBeanUtil.getBean(DataSource.class);
		Connection con = null;
		Statement stat = null;
		ResultSet result = null;
		try {
			con = db.getConnection();
			stat = con.createStatement();
			result = stat.executeQuery(sql);
			if (result != null && result.next()) {
				long c = result.getLong("total");
				if (c > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw new SQLException("执行sql脚本错误");
		} finally {
			if (result != null) {
				result.close();
			}
			if (stat != null) {
				stat.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return false;
	}

	// 初始化执行脚本
	protected final void excuteSql(String sql) throws Exception {
		DataSource db = SpringBeanUtil.getBean(DataSource.class);
		Connection con = null;
		Statement stat = null;
		try {
			con = db.getConnection();
			stat = con.createStatement();
			stat.executeUpdate(sql);
			stat.close();
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw new SQLException("执行sql脚本错误");
		} finally {
			if (stat != null) {
				stat.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}
}
