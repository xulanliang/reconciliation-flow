package com.yiban.rec.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.service.BatchService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.RandomCodeUtil;

@RestController
@RequestMapping("/api/thread")
public class ThreadInsert extends FrameworkController{
    
    @Autowired
    private BatchService batchService;
    
    final static String[] recPayTypes = new String[] {"0149","0249","0349","0449"};
    final static String[] billSources = new String[] {"self","self_jd","self_td_jd","third"};
    final static String[] patTypes = new String[] {"mz","zy","qt"};
    final static String[] orderStates = new String[] {"0156","0256"};
    final static String[] uploadOrderStatus = new String[] {"1809304","1809302","1809303","1809300"};
    final static String[] payBusinessTypes = new String[] {"0051","0151","0251","0351","0451","0551"};
    final static String[] patientNames = new String[] {"张三","李四","王五","赵六","田七","simisi"};
    final static String[] settlementTypes = new String[] {"0031","0131","0231","0331"};
	
	@GetMapping(value = "/test")
	public String test() {
	    Long startTime = System.currentTimeMillis();
	    final int CountDownNum = 200;
	    final CountDownLatch countDownLatch = new CountDownLatch(CountDownNum);
		for (int i = 0; i < CountDownNum; i++) {
            new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<HisTransactionFlow> list1 = new ArrayList<>(1650);
//                            List<OrderUpload> list2 = new ArrayList<>(1650);
                            List<ThirdBill> list3 = new ArrayList<>(1650);
                            for (int j = 0; j < 1000; j++) {
                                list1.add(getHisTransactionFlow());
//                                list2.add(getOrderUpload());
                                list3.add(getThirdBill());
                            }
                            batchService.batchInsertHisTransactionFlow(list1);
//                            batchService.batchInsertOrderUpload(list2);
                            batchService.batchInsertThirdBill(list3);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                }
            ).start();
		}
		try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		Long endTime = System.currentTimeMillis();
		System.err.println("线程执行完成，用时：" + (endTime - startTime));
		return "线程执行完成，用时：" + (endTime - startTime);
	}
	
	/**
	 * 支付结果上送
	 * @return
	 * OrderUpload
	 */
	private static OrderUpload getOrderUpload() {
	    OrderUpload t = new OrderUpload();
        t.setOrgCode("53076");
        t.setOutTradeNo(String.valueOf(Math.abs(new Random(System.currentTimeMillis()).nextLong())));
        t.setTsnOrderNo(RandomCodeUtil.generateWord(32));
        t.setPayTotalAmount(new BigDecimal(String.valueOf(Math.floor((new Random().nextDouble() * 10000.00)))));
        t.setPayAmount(new BigDecimal(String.valueOf(Math.floor((new Random().nextDouble() * 10000.00)))));
        t.setOrderState(uploadOrderStatus[(int) (new Random().nextDouble() * 4)]);
        t.setPayType(recPayTypes[(int) (new Random().nextDouble() * 4)]);
        t.setBillSource(billSources[(int) (new Random().nextDouble() * 4)]);
        t.setPatType(patTypes[(int) (new Random().nextDouble() * 3)]);
        t.setTradeDateTime(randomDate());
        t.setTradeDate(t.getTradeDateTime().substring(0,10));
        t.setPayBusinessType(payBusinessTypes[(int) (new Random().nextDouble() * 6)]);
        t.setPatientCardNo(String.valueOf(Math.abs(new Random(System.currentTimeMillis()).nextLong())));
        t.setPatientName(patientNames[(int) (new Random().nextDouble() * 6)]);
        t.setCashier("Test");
        t.setSettlementType(settlementTypes[(int) (new Random().nextDouble() * 4)]);
        return t;
    }
	
	private static HisTransactionFlow getHisTransactionFlow() {
	    HisTransactionFlow t = new HisTransactionFlow();
	    t.setOrgNo("53076");
	    t.setOrgName("滁州市第一人民医院");
	    t.setPayType(recPayTypes[(int) (new Random().nextDouble() * 4)]);
	    t.setPayFlowNo(String.valueOf(Math.abs(new Random(System.currentTimeMillis()).nextLong())));
	    t.setBillSource(billSources[(int) (new Random().nextDouble() * 4)]);
	    t.setPayAmount(new BigDecimal(String.valueOf(Math.floor((new Random().nextDouble() * 10000.00)))));
	    t.setTradeDatatime(DateUtil.stringLineToDateTime(randomDate()));
	    t.setPatType(patTypes[(int) (new Random().nextDouble() * 3)]);
	    t.setOrderState(orderStates[(int) (new Random().nextDouble() * 2)]);
	    t.setPayBusinessType(payBusinessTypes[(int) (new Random().nextDouble() * 6)]);
	    return t;
	}
	
    private static ThirdBill getThirdBill() {
        ThirdBill t = new ThirdBill();
        t.setOrgNo("53076");
        t.setOrgName("滁州市第一人民医院");
        t.setPayType(recPayTypes[(int) (new Random().nextDouble() * 4)]);
        t.setRecPayType(recPayTypes[(int) (new Random().nextDouble() * 4)]);
        t.setPayFlowNo(String.valueOf(Math.abs(new Random(System.currentTimeMillis()).nextLong())));
        t.setBillSource(billSources[(int) (new Random().nextDouble() * 4)]);
        t.setPayAmount(new BigDecimal(String.valueOf(Math.floor((new Random().nextDouble() * 10000.00)))));
        t.setTradeDatatime(DateUtil.stringLineToDateTime(randomDate()));
        t.setPatType(patTypes[(int) (new Random().nextDouble() * 3)]);
        t.setOrderState(orderStates[(int) (new Random().nextDouble() * 2)]);
        return t;
    }
	
	/**
	 * 生成随机日期
	 * @return
	 * String
	 */
    private static String randomDate() {
        int year = 2018;
        Random rndMonth = new Random();
        int month = rndMonth.nextInt(12) + 1;
        Random rndDay = new Random();
        int Day = rndDay.nextInt(30) + 1;
        Random rndHour = new Random();
        int hour = rndHour.nextInt(23);
        Random rndMinute = new Random();
        int minute = rndMinute.nextInt(60);
        Random rndSecond = new Random();
        int second = rndSecond.nextInt(60);
        return year + "-" + cp(month) + "-" + cp(Day) + " " + cp(hour) + ":" + cp(minute) + ":" + cp(second);
    }
    
    /**
     * 结构化时间单位
     * @param num
     * @return
     * String
     */
    private static String cp(int num) {
        String Num = num + "";
        if (Num.length() == 1) {
            return "0" + Num;
        } else {
            return Num;
        }
    }
    
}
