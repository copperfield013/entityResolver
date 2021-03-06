package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.datacenter.entityResolver.EntityConstants;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;

public class ArrayItemPropertyParser extends AbstractEntityPropertyParser{

	
	private final AbstractEntityPropertyParser moduleParser;
	private final String compositeName;
	private final int itemIndex;

	public ArrayItemPropertyParser(AbstractEntityPropertyParser moduleParser, String compositeName, 
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
		return (String) getProperty(compositeName + "." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, AttributeValueType.STRING);
	}
	
	
	public String getCodeName() {
		return compositeName + "[" + itemIndex + "]" + "." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL;
	}
	
	/**
	 * 当propertyName不以compositeName开头时，则强制添加compositeName
	 */
	@Override
	public Object getProperty(String propertyName, AttributeValueType propType) {
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
		}else if(propertyName.endsWith("." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL)){
			return compositeName + "[" + this.itemIndex + "]" + "." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL;
		}else if(propertyName.endsWith("." + EntityConstants.LABEL_KEY)){
			return compositeName + "[" + this.itemIndex + "]" + "." + EntityConstants.LABEL_KEY;
		}else if(propertyName.endsWith("." + ABCNodeProxy.UPDATETIME_PROPERTY_NAME)){
			return compositeName + "[" + this.itemIndex + "]" + "." + ABCNodeProxy.UPDATETIME_PROPERTY_NAME;
		}else {
			return null;
		}
	}

	public int getItemIndex() {
		return this.itemIndex;
	}
	

	

}
