package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;

import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;

public class RabcModuleEntityPropertyParser extends CommonModuleEntityPropertyParser{
	
	private String relationLabel;
	
	public RabcModuleEntityPropertyParser(FusionContextConfig config, EntityBindContext context,
			Map<String, FieldParserDescription> fieldMap, Object userPrinciple, Object propertyGetterArgument) {
		super(config, context, fieldMap, userPrinciple, propertyGetterArgument);
	}

	public String getRelationLabel() {
		return relationLabel;
	}

	public void setRelationLabel(String relationLabel) {
		this.relationLabel = relationLabel;
	}

}
