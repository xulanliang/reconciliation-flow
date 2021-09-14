package com.yiban.rec.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.dao.RefundUploadDao;
import com.yiban.rec.domain.RefundUpload;
import com.yiban.rec.service.RefundUploadService;

@Service
public class RefundUploadServiceImpl implements RefundUploadService {

    @Autowired
    private RefundUploadDao refundUploadDao;
    
    @Override
    public RefundUpload findByRefundOrderNo(String refundOrderNo) {
        return refundUploadDao.findByRefundOrderNo(refundOrderNo);
    }

    @Override
    @Transactional
    public RefundUpload save(RefundUpload refundUpload) {
        return refundUploadDao.save(refundUpload);
    }

}
