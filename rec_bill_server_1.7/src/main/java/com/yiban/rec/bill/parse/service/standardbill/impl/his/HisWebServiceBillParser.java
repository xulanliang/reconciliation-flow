package com.yiban.rec.bill.parse.service.standardbill.impl.his;

import java.util.List;

import org.apache.axis.client.Call;

import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;

public abstract class HisWebServiceBillParser<T> extends  AbstractHisBillParser<T> {
	
	private Call hisCall;
	
	public String  startTime="";
	public String  endTime="";
	public String  hisOrgCode="";

	/*protected List<T> getHisList(String sTime, String eTime, String orgCode) throws BillParseException {
		startTime=sTime;
		endTime=eTime;
		try {
			getCall();
		} catch (Exception e) {
			throw new BillParseException(e.getMessage());
		}
		return getList(hisCall);
	}*/
	
	protected List<T> getHisList(String sTime, String eTime, String orgCode) throws BillParseException {
		startTime=sTime;
		endTime=eTime;
		hisOrgCode=orgCode;
		return getList();
	}
	
	public abstract List<T> getList() throws BillParseException;
}
