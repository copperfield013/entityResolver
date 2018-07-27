package cn.sowell.datacenter.entityResolver.format;

import java.util.Date;

import com.abc.util.ValueType;

import cn.sowell.copframe.spring.file.FileHaunt;

public class PropertyFormatFactory {

	private PropertyFormat fileHauntPropertyFormat = new FileHauntPropertyFormat();

	public PropertyFormat getFormat(Object value, ValueType propType, String format) {
		if(value instanceof FileHaunt) {
			return fileHauntPropertyFormat;
		}
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

