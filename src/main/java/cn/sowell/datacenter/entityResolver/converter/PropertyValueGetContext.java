package cn.sowell.datacenter.entityResolver.converter;

import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public interface PropertyValueGetContext {
	ModuleEntityPropertyParser getParser();
	EntityBindContext getRootContext();
	FieldParserDescription getField();
	String getFullPropertyKey();
	String getFullPropertyPath();
	EntityBindContext getCurrentContext();
	String getCurrentPropertyPath();
	FusionContextConfig getContextConfig();
	EntityBindContext getParentEntityContext();
}
