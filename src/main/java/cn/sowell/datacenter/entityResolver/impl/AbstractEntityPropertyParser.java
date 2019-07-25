package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;
import java.util.Set;

import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.copframe.utils.Assert;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.GetonlyMap;
import cn.sowell.datacenter.entityResolver.format.PropertyFormat;
import cn.sowell.datacenter.entityResolver.format.PropertyFormatFactory;

abstract class AbstractEntityPropertyParser implements CEntityPropertyParser {
	
	
	protected Map<String, FieldParserDescription> fieldMap;
	
	private final PropertyFormatFactory formatFactory;
	
	public AbstractEntityPropertyParser(Map<String, FieldParserDescription> fieldMap) {
		super();
		Assert.notNull(fieldMap);
		this.fieldMap = fieldMap;
		formatFactory = new PropertyFormatFactory();
	}

	@Override
	public Map<String, Object> getPmap() {
		return propertyGetMap;
	}
	
	@Override
	public Map<String, String> getSmap() {
		return propertyFormatMap;
	}
	
	@Override
	public Object getProperty(String propertyName) {
		return getProperty(propertyName, null);
	}
	
	@Override
	public String getFormatedProperty(String propertyName, AttributeValueType propType) {
		return getFormatedProperty(propertyName, propType, null);
	}
	
	@Override
	public String getFormatedProperty(String propertyName, AttributeValueType propType, String format) {
		Object value = getProperty(propertyName, propType);
		String result = null;
		try {
			PropertyFormat pFormat = formatFactory.getFormat(value, propType, format);
			result = pFormat.format(value);
		}catch(Exception e) {
			result = FormatUtils.toString(value);
		}
		return result;
	}


	@Override
	public String getFormatedProperty(String propertyName) {
		if(fieldMap != null) {
			String fieldFullName = propertyName.replaceAll("\\[\\d+\\]", "");
			FieldParserDescription field = fieldMap.get(fieldFullName);
			if(field != null) {
				return getFormatedProperty(propertyName, field.getAbcType());
			}
		}
		return getFormatedProperty(propertyName, null);
	}
	
	private Map<String, Object> propertyGetMap = new GetonlyMap<String, Object>() {

		@Override
		public Object get(Object key) {
			return getProperty((String) key);
		}
	};
	
	private Map<String, String> propertyFormatMap = new GetonlyMap<String, String>() {

		@Override
		public String get(Object key) {
			return getFormatedProperty((String) key);
		}
		
		@Override
		public boolean containsKey(Object key) {
			return fieldMap.containsKey(key);
		}
		
		@Override
		public Set<String> keySet() {
			return fieldMap.keySet();
		}
	};

}
