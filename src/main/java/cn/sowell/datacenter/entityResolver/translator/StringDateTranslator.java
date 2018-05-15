package cn.sowell.datacenter.entityResolver.translator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.sowell.datacenter.entityResolver.PropertyTranslator;

public class StringDateTranslator implements PropertyTranslator<String, Date> {

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public boolean check(String propValue) {
		try {
			return propValue == null || dateFormat.parse(propValue) != null;
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public Date transfer(String propValue) {
		try {
			return dateFormat.parse(propValue);
		} catch (ParseException e) {
			return null;
		}
	}

}
