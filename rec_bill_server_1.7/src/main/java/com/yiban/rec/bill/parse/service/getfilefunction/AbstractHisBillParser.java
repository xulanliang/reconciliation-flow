package com.yiban.rec.bill.parse.service.getfilefunction;

import java.util.List;

import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;

public abstract class AbstractHisBillParser<T> extends AbstractBillParser<T> {

	@Override
	protected List<T> doParse(String orgCode, String date) throws BillParseException {
		return getHisList(date,date,orgCode);
	}

	protected abstract List<T> getHisList(String startTime,String endTime,String orgCode)throws BillParseException;

}
