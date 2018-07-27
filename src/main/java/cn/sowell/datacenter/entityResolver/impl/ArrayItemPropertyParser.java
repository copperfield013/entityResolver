package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

import com.abc.util.ValueType;

import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public class ArrayItemPropertyParser extends AbstractEntityPropertyParser{

	
	private final ModuleEntityPropertyParser moduleParser;
	private final String compositeName;
	private final int itemIndex;

	public ArrayItemPropertyParser(ModuleEntityPropertyParser moduleParser, String compositeName, 
			int itemIndex,
			Map<String, FieldParserDescription> fieldMap) {
		super(fieldMap);
		Assert.notNull(moduleParser);
		Assert.hasText(compositeName);
		this.moduleParser = moduleParser;
		this.compositeName = compositeName;
		this.itemIndex = itemIndex;
	}

	@Override
	public String getCode() {
		return (String) getProperty(compositeName + "." + ABCNodeProxy.CODE_PROPERTY_NAME, ValueType.STRING);
	}
	
	
	public String getCodeName() {
		return compositeName + "[" + itemIndex + "]" + "." + ABCNodeProxy.CODE_PROPERTY_NAME;
	}
	
	/**
	 * 当propertyName不以compositeName开头时，则强制添加compositeName
	 */
	@Override
	public Object getProperty(String propertyName, ValueType propType) {
		return moduleParser.getProperty(getFieldName(propertyName), propType);
	}
	
	static Pattern pattern = Pattern.compile("^\\[\\d+\\](.+)$");
	private String getFieldName(String propertyName) {
		if(propertyName.startsWith(compositeName)) {
			String suffix = propertyName.substring(compositeName.length());
			Matcher matcher = pattern.matcher(suffix);
			if(matcher.matches()) {
				propertyName = compositeName + matcher.group(1);
			}
		}else {
			propertyName = compositeName + "." + propertyName;
		}
		FieldParserDescription fieldDesc = fieldMap.get(propertyName);
		if(fieldDesc != null) {
			return fieldDesc.getArrayFieldNameFormat(this.itemIndex);
		}else if(propertyName.endsWith("." + ABCNodeProxy.CODE_PROPERTY_NAME)){
			return compositeName + "[" + this.itemIndex + "]" + "." + ABCNodeProxy.CODE_PROPERTY_NAME;
		}else if(propertyName.endsWith("." + RelationEntityProxy.LABEL_KEY)){
			return compositeName + "[" + this.itemIndex + "]" + "." + RelationEntityProxy.LABEL_KEY;
		}else {
			return null;
		}
	}
	

	

}
