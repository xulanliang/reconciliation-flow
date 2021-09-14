package com.yiban.rec.web.admin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.rec.util.StringUtil;

/**
 * 
 * @ClassName: SMSCallBackController
 * @Description: 短信回调
 * @author tuchun@clearofchina.com
 * @date 2017年3月29日 下午5:05:20
 * @version V1.0
 *
 */
@Controller
@RequestMapping("/sms")
public class SMSCallBackController extends CurrentUserContoller {
	@RequestMapping(value = "/report", method = RequestMethod.POST)
	@ResponseBody
	public Integer list(@RequestParam(value = "args", required = true) String args) throws BusinessException {
		// Report
		// ID，特服号，手机号，唯一标识，状态（0或DELIVRD为成功，其它均为失败），时间，如有多条，以英文“;”隔开，最多1000条
		// 123456,62891,159*404,564687,DELIVRD,2009-10-19
		// 13:01:36;1127865,62891,189*404,420937,DELIVRD,2009-10-19 13:01:42
		try {
			if (StringUtil.isEmpty(args)) {
				return -1;
			}
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(20);
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						//模拟业务场景耗时15秒
						Thread.sleep(15*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String[] argsArr = args.split(";");
					for (String arg : argsArr) {
						String[] argArr = arg.split(",");
						String msg = "";
						for (int i = 0; i < argArr.length; i++) {
							if (i == 0) {
								msg = msg + "Report ID:" + argArr[i] + "  ";
							} else if (i == 1) {
								msg = msg + "特服号:" + argArr[i] + "  ";
							} else if (i == 2) {
								msg = msg + "手机号:" + argArr[i] + "  ";
							} else if (i == 3) {
								msg = msg + "唯一标识:" + argArr[i] + "  ";
							} else if (i == 4) {
								msg = msg + "状态:" + argArr[i] + "  ";
							} else if (i == 5) {
								msg = msg + "时间:" + argArr[i] + "  ";
							}
						}
						System.out.println("sms-msg:" + msg);
						logger.warn("sms-msg:" + msg);

					}
				}
			});
			cachedThreadPool.shutdown();
			return 0;
		} catch (Exception e) {
			return -2;
		}
		
	}
}
