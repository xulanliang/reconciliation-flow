package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.dao.WindowCashDao;
import com.yiban.rec.domain.WindowCash;
import com.yiban.rec.domain.vo.SZSRHisCashVo;
import com.yiban.rec.domain.vo.WindowCashCheckVo;
import com.yiban.rec.service.WindowCashCheckService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.StringUtil;

import net.sf.json.JsonConfig;

/**
 * @Description
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-02-21 14:57
 */
@Service
@Transactional(readOnly = true)
public class WindowCashCheckServiceImpl extends BaseOprService implements WindowCashCheckService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WindowCashDao windowCashDao;

    private String url = "http://218.18.109.226:8083/opss_web/mock/formal/xyb_all_cyrj/0";

    @Override
    public Page getCashCheckData(WindowCashCheckVo cashCheckVo, List<Organization> orgList, PageRequest pageRequest) {

        String orgNo = cashCheckVo.getOrgCode();
        String[] orgs = getOrgNoList(orgList, orgNo);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Page page = null;
        try {
            page = windowCashDao.findByOrgNoAndCashDateAndBankType(orgs, sdf.parse(cashCheckVo.getStartDate()), sdf.parse(cashCheckVo.getEndDate()),
                    "%" + cashCheckVo.getCashierName() + "%", "%" + cashCheckVo.getBankType() + "%", "%" + cashCheckVo.getBusinessType() + "%", pageRequest);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return page;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveCashCheckData(WindowCashCheckVo windowCashCheckVo, User currentUser) {
        WindowCash windowCash = new WindowCash();
        if (!StringUtil.isNullOrEmpty(windowCashCheckVo.getId())) {
            windowCash.setId(Long.valueOf(windowCashCheckVo.getId()));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        windowCash.setOrgCode(windowCashCheckVo.getOrgCode());
        try {
            windowCash.setCashDate(sdf.parse(windowCashCheckVo.getCashDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // ??????His?????????????????????????????????
        BigDecimal hisAmount = getHisCasherAmount(windowCashCheckVo.getCashDate(),currentUser.getLoginName());
        windowCash.setHisAmount(hisAmount);
        // ????????????
        String channelAmount = (windowCashCheckVo.getChannelAmount() == null || windowCashCheckVo.getChannelAmount().trim().equals("")) ? "0" : windowCashCheckVo.getChannelAmount().trim();
        BigDecimal channelAmountMoney = new BigDecimal(channelAmount);
        // ????????????????????????
        if(channelAmountMoney.compareTo(hisAmount)==0){
            // ????????????  ??????
            windowCash.setCashStatus(CommonEnum.CashCheckState.NORMAL.getValue().toString());
        }else {
            // ??????
            windowCash.setCashStatus(CommonEnum.CashCheckState.UNUSUAL.getValue().toString());
        }




        // ???????????????
        windowCash.setCashierAccount(currentUser.getLoginName());
        windowCash.setCashierName(currentUser.getName());
        windowCash.setBusinessType(windowCashCheckVo.getBusinessType());
        windowCash.setBankType(windowCashCheckVo.getBankType());
        // ????????????
        windowCash.setChannelAmount(channelAmountMoney);
        windowCash.setExceptionalAmount(new BigDecimal(windowCashCheckVo.getExceptionalAmount()));
        windowCash.setExceptionalReason(windowCashCheckVo.getExceptionalReason());
        windowCash.setCreatedDateTime(new Date());
        windowCashDao.save(windowCash);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateCashCheckDataState(WindowCashCheckVo windowCashCheckVo, User currentUser) {
        windowCashDao.updateById(CommonEnum.CashCheckState.ADOPT.getValue().toString(), new Date(), currentUser.getLoginName(), currentUser.getName(), Long.parseLong(windowCashCheckVo.getId()));
    }

    /**
     * ??????His???????????????His??????????????????
     *
     * @param date
     * @param casherName
     */
    public BigDecimal getHisCasherAmount(String date, String casherName) {
        BigDecimal totalMoney = new BigDecimal("0");
        String[] branchCodeArr = {"0", "01"};
        date = date.split(" ")[0];
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        String servname = "getHisXJZFData";
        String operCode = "";
        for (String branchCode : branchCodeArr) {
            String requestXmlStr = null;
            logger.info("#### ??????His??????????????????url???{}????????????{}", url, requestXmlStr);
            String result = HttpClientUtil.doPostXml(url, requestXmlStr);
            // ??????????????????
            totalMoney = totalMoney.add(parseResult(result, casherName));
        }
        return totalMoney;
    }

    private BigDecimal parseResult(String result, String casherName) {
        String unescapeResult = StringEscapeUtils.unescapeXml(result);
        String regex = "<respons>[\\s\\S]+</respons>";
        String response = null;
        // ?????????????????????????????????????????????
        Pattern p = Pattern.compile(regex);
        // ???????????????
        Matcher m = p.matcher(unescapeResult);
        while (m.find()) {
            response = m.group();
        }
        JSONObject root = new JSONObject();
        String resultCode = null;
        try {
            JSONObject json = XML.toJSONObject(response);
            logger.info("????????????HIS XML???json???????????????:" + json);
            root = json.getJSONObject("respons");
            resultCode = String.valueOf(root.get("resultCode"));
        } catch (JSONException e) {
            logger.error("XML?????????JSON????????????", e);
        }
        if (resultCode.equals(CommonConstant.SZSR_TRADE_CODE_SUCCESS)) {
            // ????????????
            net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(root.toString());
            net.sf.json.JSONObject resData = null;
            net.sf.json.JSONArray array = null;
            resData = obj.getJSONObject("result");
            try {
                String item = resData.get("item").toString();
                if (item.startsWith("[")) {
                    array = resData.getJSONArray("item");
                } else {
                    array = new net.sf.json.JSONArray();
                    array.add(resData.getJSONObject("item"));
                }
            } catch (Exception e) {
                array = new net.sf.json.JSONArray();
                array.add(obj.get("response"));
                logger.error("JSON?????????????????????", e.getMessage());
            }
            @SuppressWarnings("unchecked")
            List<SZSRHisCashVo> listData = net.sf.json.JSONArray.toList(array, new SZSRHisCashVo(), new JsonConfig());
            BigDecimal moneyAmount = getMoneyAmount(listData, casherName);
            return moneyAmount;
        } else {
            // ??????????????????
            String resultMessage = (String) root.get("resultMessage");
            logger.info("?????????????????????{}", resultMessage);
            return new BigDecimal("0");
        }
    }

    /**
     * @param listData
     * @return
     */
    private BigDecimal getMoneyAmount(List<SZSRHisCashVo> listData, String casherName) {
        BigDecimal totalMoney = new BigDecimal("0");
        for (SZSRHisCashVo szsrHisCashVo : listData) {
            // ??????????????????????????????
            String czgh = szsrHisCashVo.getCZGH();
            if(!czgh.equals(casherName)){
                continue;
            }
            String moneyStr = "".equals(szsrHisCashVo.getXJJE()) ? "0" : szsrHisCashVo.getXJJE();
            totalMoney = totalMoney.add(new BigDecimal(moneyStr));
        }
        return totalMoney;
    }

    /**
     * ??????????????????
     *
     * @param orgList
     * @param orgNo
     * @return
     */
    public String[] getOrgNoList(List<Organization> orgList, String orgNo) {
        String[] orgs = null;
        if (orgList != null && orgList.size() > 0) {
            orgs = new String[orgList.size() + 1];
            orgs[0] = orgNo;
            for (int i = 0; i < orgList.size(); i++) {
                orgs[i + 1] = orgList.get(i).getCode();
            }
        } else {
            orgs = new String[1];
            orgs[0] = orgNo;
        }
        return orgs;
    }

}
