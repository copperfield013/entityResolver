package cn.sowell.datacenter.entityResolver.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FloatPropertyFormat implements PropertyFormat{

	NumberFormat numberFormat;
	
	public FloatPropertyFormat(String format) {
		this.numberFormat = new DecimalFormat(format);
	}

	@Override
	public String format(Object value) {
		return this.numberFormat.format(value);
	}

}
