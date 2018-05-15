package cn.sowell.datacenter.entityResolver.format;

import java.util.Date;

import cn.sowell.copframe.utils.FormatUtils;

public class CommonPropertyFormat implements PropertyFormat {

	DatePropertyFormat dateFormat = new DatePropertyFormat("yyyy-MM-dd");
	FloatPropertyFormat floatFormat = new FloatPropertyFormat("0.00");
	
	
	@Override
	public String format(Object value) {
		if(value instanceof Date) {
			return dateFormat.format(value);
		}else if(value instanceof Float || value instanceof Double) {
			return floatFormat.format(value);
		}else {
			return FormatUtils.toString(value);
		}
	}

}
