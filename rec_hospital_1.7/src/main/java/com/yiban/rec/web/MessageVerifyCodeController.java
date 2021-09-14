package com.yiban.rec.web;

import com.yiban.framework.account.domain.MessageVerifyCodeReqVo;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.MessageResVoTest;
import com.yiban.rec.service.MessageVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * describe: 登录页短信验证码
 *
 * @author xll
 * @date 2020/09/11
 */
@RestController
@RequestMapping("api/message/verify/code")
public class MessageVerifyCodeController {

    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private MessageVerifyCodeService messageVerifyCodeService;


    /**
     * 获取登录页面是否需要短信验证码信息，默认不需要
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult hasMessageVerifyCode() {
        ResponseResult responseResult = messageVerifyCodeService.hasMessageVerifyCode();
        return responseResult;
    }

    /**
     * 获取短信验证码
     *
     * @return
     */
    @RequestMapping(value = "get", method = RequestMethod.POST)
    public ResponseResult getMessageVerifyCode(@RequestBody MessageVerifyCodeReqVo reqVo) {
        ResponseResult responseResult = messageVerifyCodeService.getMessageVerifyCode(reqVo);
        return responseResult;
    }

    /**
     * 获取短信验证码
     *
     * @return
     */
    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public MessageResVoTest test() {
        System.out.println("放心，服务正常运行了");
        return new MessageResVoTest();
    }

}
