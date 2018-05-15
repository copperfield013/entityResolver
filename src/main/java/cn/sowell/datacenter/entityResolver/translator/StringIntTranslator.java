package cn.sowell.datacenter.entityResolver.translator;

import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.PropertyTranslator;

public class StringIntTranslator implements PropertyTranslator<String, Integer> {

	@Override
	public boolean check(String propValue) {
		return propValue == null || propValue.matches("^\\d+$");
	}

	@Override
	public Integer transfer(String propValue) {
		return FormatUtils.toInteger(propValue);
	}

}
