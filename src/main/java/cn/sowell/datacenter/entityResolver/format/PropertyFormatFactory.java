package cn.sowell.datacenter.entityResolver.format;

import java.util.Date;

public class PropertyFormatFactory {

	public PropertyFormat getFormat(Object value, String propType, String format) {
		if(format != null) {
			if(value instanceof Date) {
				return new DatePropertyFormat(format);
			}else if(value instanceof Float || value instanceof Double) {
				return new FloatPropertyFormat(format);
			}
		}
		return new CommonPropertyFormat();
	}

}

