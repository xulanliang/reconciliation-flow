package com.yiban.rec.service;

import com.yiban.framework.core.service.ServiceException;
import com.yiban.rec.domain.vo.HisRequestVo;
import com.yiban.rec.domain.vo.HisResponseVo;

/**
 * his相关接口实现
 * @Author WY
 * @Date 2018年10月8日
 */
public interface HisService {

    /**
     * 调用HIS接口
     * @param vo
     * @return
     * @throws ServiceException
     * HisResponseVo
     */
    HisResponseVo service(HisRequestVo vo) throws Exception;
}
