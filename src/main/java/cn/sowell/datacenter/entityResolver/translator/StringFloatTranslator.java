package cn.sowell.datacenter.entityResolver.translator;

import cn.sowell.datacenter.entityResolver.PropertyTranslator;

public class StringFloatTranslator implements PropertyTranslator<String, Float> {

	@Override
	public boolean check(String propValue) {
		try {
			return propValue == null || Float.valueOf(propValue) != null;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Float transfer(String propValue) {
		return propValue == null? null: Float.valueOf(propValue);
	}


}
