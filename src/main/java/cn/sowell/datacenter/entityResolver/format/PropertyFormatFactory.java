package cn.sowell.datacenter.entityResolver.format;

import java.util.Date;

import com.abc.util.ValueType;

import cn.sowell.copframe.spring.file.FileHaunt;

public class PropertyFormatFactory {

	private PropertyFormat fileHauntPropertyFormat = new FileHauntPropertyFormat();
	private PropertyFormat datetimeFormat = new DatePropertyFormat("yyyy-MM-dd HH:mm:ss");
	private PropertyFormat dateFormat = new DatePropertyFormat("yyyy-MM-dd");
	public PropertyFormat getFormat(Object value, ValueType propType, String format) {
		if(value instanceof FileHaunt) {
			return fileHauntPropertyFormat;
		}
		if(propType != null) {
			if(value instanceof Date) {
				if(format != null) {
					return new DatePropertyFormat(format);
				}else {
					if(ValueType.DATETIME.equals(propType)) {
						return datetimeFormat;
					}else {
						return dateFormat;
					}
				}
			}else if(value instanceof Float || value instanceof Double) {
				return new FloatPropertyFormat(format);
			}
		}
		return new CommonPropertyFormat();
	}

}

