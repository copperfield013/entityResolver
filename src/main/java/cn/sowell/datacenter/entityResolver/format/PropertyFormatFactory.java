package cn.sowell.datacenter.entityResolver.format;

import java.util.Date;

import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.copframe.spring.file.FileHaunt;

public class PropertyFormatFactory {

	private PropertyFormat fileHauntPropertyFormat = new FileHauntPropertyFormat();
	private PropertyFormat datetimeFormat = new DatePropertyFormat("yyyy-MM-dd HH:mm:ss");
	private PropertyFormat dateFormat = new DatePropertyFormat("yyyy-MM-dd");
	@SuppressWarnings("unused")
	private PropertyFormat yearMonthFormat = new DatePropertyFormat("yyyy-MM");
	public PropertyFormat getFormat(Object value, AttributeValueType propType, String format) {
		if(value instanceof FileHaunt) {
			return fileHauntPropertyFormat;
		}
		if(propType != null) {
			if(value instanceof Date) {
				if(format != null) {
					return new DatePropertyFormat(format);
				}else {
					if(AttributeValueType.DATETIME.equals(propType)) {
						return datetimeFormat;
					}/*else if(){
						return yearMonthFormat;
					}*/else {
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

