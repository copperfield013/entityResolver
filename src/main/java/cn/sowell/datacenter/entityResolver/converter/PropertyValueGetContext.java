package cn.sowell.datacenter.entityResolver.converter;

import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.impl.EntityPropertyParser;

public interface PropertyValueGetContext {
	EntityPropertyParser getParser();
	EntityBindContext getRootContext();
	FieldParserDescription getField();
	String getFullPropertyKey();
	String getFullPropertyPath();
	EntityBindContext getCurrentContext();
	String getCurrentPropertyPath();
	FusionContextConfig getContextConfig();
	EntityBindContext getParentEntityContext();
	Object getUserPrinciple();
	String getRelationName();
	Object getPropertyGetterArgument();
}
