package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;

import org.springframework.util.Assert;

import com.abc.application.BizFusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.RelationNode;
import com.abc.util.ValueType;

import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;

public class RelSelectionEntityPropertyParser extends EntityPropertyParser{

	
	private String relationName;

	RelSelectionEntityPropertyParser(
			//config是根配置文件的config
			FusionContextConfig config,
			String relationName,
			//context是Relation节点的上下文，包含的entity是Relation下的ABCNode
			Map<String, FieldParserDescription> fieldMap, 
			Object userPrinciple, Entity entity) {
		super(config, getContext(config, relationName, entity, userPrinciple), fieldMap, userPrinciple);
		Assert.hasText(relationName);
		this.relationName = relationName;
	}
	
	private static EntityBindContext getContext(FusionContextConfig config, String relationName, Entity entity, Object user) {
		BizFusionContext c = config.getCurrentContext(user);
		ABCNode rootNode = c.getABCNode();
		RelationNode relNode = rootNode.getRelation(relationName);
		return new ABCNodeEntityBindContext(relNode.getAbcNode(), entity);
	}

	@Override
	public Object getProperty(String propertyName, ValueType propType) {
		if(propertyName != null && propertyName.startsWith(relationName + ".")) {
			String suffix = propertyName.substring(relationName.length() + 1);
			if(!suffix.isEmpty()) {
				FieldParserDescription field = fieldMap.get(propertyName.replaceAll("\\[\\d+\\]", ""));
				return super.getProperty(relationName, suffix, field, propType);
			}
		}
		return null;
	}

}