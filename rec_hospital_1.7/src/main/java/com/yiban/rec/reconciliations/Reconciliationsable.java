package com.yiban.rec.reconciliations;

import java.util.List;

import com.yiban.rec.domain.HealthException;
import com.yiban.rec.domain.TradeCheckFollow;

/**
 * @author swing
 * @date 2018年7月19日 上午11:07:27
 * 服务层的对账接口(提供给service 使用,所有的实现类不应用依赖spring容器)
 */
public interface Reconciliationsable {
    /**
     * 对账业务总接口
     * @return 异常账单列表
     * @throws Exception
     */
	List<TradeCheckFollow> compareBill() throws Exception;

	List<HealthException> compareHealthBill() throws Exception;
}
