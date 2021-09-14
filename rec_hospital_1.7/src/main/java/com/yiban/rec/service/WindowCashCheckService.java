package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.rec.domain.vo.WindowCashCheckVo;

/**
 * @Description
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-02-21 14:54
 */
public interface WindowCashCheckService {

    Page<Map<String, Object>> getCashCheckData(WindowCashCheckVo windowCashCheckVo, List<Organization> orgList, PageRequest pageRequest);

    void saveCashCheckData(WindowCashCheckVo windowCashCheckVo, User currentUser);

    void updateCashCheckDataState(WindowCashCheckVo windowCashCheckVo, User currentUser);

}
