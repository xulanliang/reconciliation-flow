package com.yiban.framework.account.domain;

import java.util.Date;

/**
 * describe:
 *
 * @author xll
 * @date 2020/09/11
 */
public class MessageVerifyCodeReqVo {
    // 手机号码，多个用逗号分隔
    String mobiles;
    // 短信文本
    String message;
    // 短信验证码
    String verifyCode;
    // sysid
    String sysid;
    // 登录用户名
    String userName;
    // 创建时间
    Date getDate;
    // 验证码有效时间
    String endDateStr;

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSysid() {
        return sysid;
    }

    public void setSysid(String sysid) {
        this.sysid = sysid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getGetDate() {
        return getDate;
    }

    public void setGetDate(Date getDate) {
        this.getDate = getDate;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }
}
