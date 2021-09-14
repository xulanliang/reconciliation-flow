package com.yiban.rec.bill.parse.service.standardbill.impl.his;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.ProConfigManager;

/**
 * @Description 存储过程
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-06-03 17:51
 */
public abstract class HisProcedureBillParser<T> extends AbstractHisBillParser<T> {

    @Override
    protected List<T> getHisList(String startTime, String endTime, String orgCode) throws BillParseException {
        Connection connection = getConnection();
        return doProcedure(connection, startTime, endTime, orgCode);
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
	protected Connection getConnection() {
		try {
			String driverName = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisDriverName);
			String url = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisAdd);
			String username = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisUser);
			String password = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisPass);

			// 注册数据库驱动
			Class.forName(driverName);
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


    /**
     * 释放数据库连接资源
     *
     * @param conn
     * @param st
     * @param rs
     */
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                rs = null;
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                st = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }

    /**
     * 执行存储过程
     *
     * @param tradeDate 账单日期
     * @return
     */
    public CallableStatement callProcedure(String tradeDate) {
        return null;
    }

    public abstract List<T> doProcedure(Connection connection, String startTime, String endTime, String orgCode);

}
