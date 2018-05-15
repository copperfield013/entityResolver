package cn.sowell.datacenter.entityResolver.format;

import java.util.Date;

import cn.sowell.copframe.utils.date.CommonDateFormat;
import cn.sowell.copframe.utils.date.FrameDateFormat;

public class DatePropertyFormat implements PropertyFormat{

	FrameDateFormat dateFormat = new CommonDateFormat();
	String format;
	
	
	public DatePropertyFormat(String format) {
		this.format = format;
	}

	@Override
	public String format(Object value) {
		if(value instanceof Date) {
			return dateFormat.format((Date) value, format);
		}
		return null;
	}

}
