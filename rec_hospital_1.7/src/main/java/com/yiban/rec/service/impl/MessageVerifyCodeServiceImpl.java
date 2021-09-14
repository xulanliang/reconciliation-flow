package com.yiban.rec.service.impl;

import com.google.gson.Gson;
import com.yiban.framework.account.common.CommonContents;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.dao.UserDao;
import com.yiban.framework.account.domain.MessageVerifyCodeReqVo;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.service.MessageVerifyCodeService;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * describe: 手机短信验证码管理
 *
 * @author xll
 * @date 2020/09/11
 */
@Service
public class MessageVerifyCodeServiceImpl implements MessageVerifyCodeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Gson gson = new Gson();

    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private UserDao userDao;

    @Override
    public ResponseResult getMessageVerifyCode(MessageVerifyCodeReqVo reqVo) {
        User user = userDao.findValideUserByLoginName(reqVo.getUserName());
        if (user == null) {
            return ResponseResult.failure("当前用户不存在！");
        }
        String messageVerifyCodeTimeLimit = propertiesConfigService.findValueByPkey(ProConstants.MESSAGE_VERIFY_CODE_TIME_LIMIT,
                ProConstants.DEFAULT.get(ProConstants.MESSAGE_VERIFY_CODE_TIME_LIMIT));
        Integer timeLimit = Integer.valueOf(messageVerifyCodeTimeLimit);
        if (timeLimit > 0) {
            // 判断获取验证码是否超频
            String mobile = user.getMobilePhone();
            if (CommonContents.MESSAGE_VERIFY_CODE_MAP.containsKey(mobile)) {
                /*DateUtil.getSpecifiedDateTimeAfter(new Date(), timeLimit)*/
                // 获取验证码时间
                String endDateStr = String.valueOf(CommonContents.MESSAGE_VERIFY_CODE_MAP.get(mobile));
                Date startDate = null;
                try {
                    startDate = DateUtil.transferStringToDateFormat(endDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                endDateStr = DateUtil.getSpecifiedDateTimeAfter(startDate, timeLimit);
                String currentDateStr = DateUtil.getCurrentTimeString();
                int flag = com.yiban.framework.account.util.DateUtil.compareDate(currentDateStr, endDateStr,
                        com.yiban.framework.account.util.DateUtil.STANDARD_FORMAT);
                if (flag == -1) {
                    return ResponseResult.failure("请求频率过高，请稍后再试！");
                }
            }
        }
        // 判断短信模板
        String messageTemplate = propertiesConfigService.findValueByPkey(ProConstants.MESSAGE_TEMPLATE);
        if (StringUtil.isEmpty(messageTemplate)) {
            return ResponseResult.failure("短信模板未配置，请联系管理员！");
        }
        // 获取短信模板
        String messageVerifyCodeUrl = propertiesConfigService.findValueByPkey(ProConstants.MESSAGE_VERIFY_CODE_URL, "");
        if (StringUtil.isEmpty(messageVerifyCodeUrl)) {
            return ResponseResult.failure("短信验证码地址未配置，请联系管理员！");
        }

        // 生成随机数验证码
        String messageCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        reqVo.setVerifyCode(messageCode);
        // 短信验证码过期时间
        String verifyCodeTime = propertiesConfigService.findValueByPkey(ProConstants.MESSAGE_VERIFY_CODE_TIME,
                ProConstants.DEFAULT.get(ProConstants.MESSAGE_VERIFY_CODE_TIME));
        String timeValidity = DateUtil.getSpecifiedDateTimeAfter(new Date(), Integer.valueOf(verifyCodeTime));
        // 拼接短信模板
        messageTemplate = String.format(messageTemplate, user.getMobilePhone(), messageCode, timeValidity);
        reqVo.setEndDateStr(timeValidity);
        Map<String, String> params = new HashMap<>();
        /*params.put("mobiles", reqVo.getMobiles());*/
        params.put("mobiles", user.getMobilePhone());
        params.put("message", messageTemplate);
        params.put("sysid", "8AJ033");
        reqVo.setMobiles(user.getMobilePhone());

        String paramsJsonStr = gson.toJson(params, Map.class);
        logger.info("##### 获取短信验证码地址为：{}", messageVerifyCodeUrl);
        logger.info("##### 获取短信验证码入参为：{}", paramsJsonStr);
        String result = "";
        try {
            result = HttpClientUtil.doGet(messageVerifyCodeUrl, params);
        } catch (Exception e) {
            logger.info("##### 获取短信验证码异常：{}", e.getMessage());
            return ResponseResult.failure("获取短信验证码异常！");
        }
        logger.info("##### 获取短信验证码出参为：{}", result);
        JSONObject jsonObject = JSONObject.fromObject(result);
        String reCode = String.valueOf(jsonObject.get("rtn"));
        if (!reCode.equals("0")) {
            String desc = String.valueOf(jsonObject.get("desc"));
            return ResponseResult.failure("获取验证码异常：" + desc);
        }
        // 将验证码保存到本地内存中
        setMessageVerifyCodeData(reqVo);
        if (timeLimit > 0){
            // 更新获取验证码时间信息  timeLimit
            /*CommonContents.MESSAGE_VERIFY_CODE_MAP.put(user.getMobilePhone(), DateUtil.getSpecifiedDateTimeAfter(new Date(), timeLimit));*/
            CommonContents.MESSAGE_VERIFY_CODE_MAP.put(user.getMobilePhone(), DateUtil.getCurrentTimeString());
        }
        return ResponseResult.success("获取验证码成功");
    }

    /**
     * 将验证码保存到本地内存中
     *
     * @param reqVo
     */
    private void setMessageVerifyCodeData(MessageVerifyCodeReqVo reqVo) {
        String key = reqVo.getUserName() + "_" + reqVo.getMobiles();
        /*String key = "admin" + "_" + reqVo.getMobiles() + "_" + "123456";*/
        reqVo.setGetDate(new Date());
        CommonContents.MESSAGE_VERIFY_CODE_MAP.put(key, reqVo);
    }

    @Override
    public ResponseResult hasMessageVerifyCode() {
        boolean messageVerifyCode = Boolean.valueOf(propertiesConfigService.findValueByPkey(ProConstants.MESSAGE_VERIFY_CODE,
                ProConstants.DEFAULT.get(ProConstants.MESSAGE_VERIFY_CODE)));
        if (messageVerifyCode) {
            return ResponseResult.success("需要短信验证码");
        } else {
            return ResponseResult.failure("不需要短信验证码");
        }
    }

}
