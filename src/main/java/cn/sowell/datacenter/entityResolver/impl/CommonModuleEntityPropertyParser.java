package cn.sowell.datacenter.entityResolver.impl;


import java.util.List;
import java.util.Map;

import com.abc.dto.ErrorInfomation;
import com.abc.util.ValueType;

import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

class CommonModuleEntityPropertyParser extends EntityPropertyParser implements ModuleEntityPropertyParser{

	private List<ErrorInfomation> errors;
	
	
	CommonModuleEntityPropertyParser(FusionContextConfig config, EntityBindContext context, Map<String, FieldParserDescription> fieldMap, Object userPrinciple) {
		this(config, context, fieldMap, userPrinciple, null);
	}
	
	public CommonModuleEntityPropertyParser(FusionContextConfig config, EntityBindContext context,
			Map<String, FieldParserDescription> fieldMap, Object userPrinciple, Object propertyGetterArgument) {
		super(config, context, fieldMap, userPrinciple, propertyGetterArgument);
	}

	@Override
	public Object getProperty(String propertyName, ValueType propType) {
		FieldParserDescription field = fieldMap.get(propertyName.replaceAll("\\[\\d+\\]", ""));
		return getProperty(null, propertyName, field, propType);
	}
	
	@Override
	public String getCode() {
		return this.getId();
	}
	
	@Override
	public String getId() {
		return (String) getProperty(config.getCodeAttributeName());
	}
	
	@Override
	public String getTitle() {
		return (String) getProperty(config.getTitleAttributeName());
	}

	@Override
	public List<ErrorInfomation> getErrors() {
		return errors;
	}
	
	@Override
	public void setErrors(List<ErrorInfomation> errors) {
		this.errors = errors;
	}

	
}
