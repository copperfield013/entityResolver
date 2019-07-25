package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;

import org.springframework.util.Assert;

import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.hc.HCFusionContext;
import cho.carbon.meta.enun.AttributeValueType;
import cho.carbon.meta.struc.er.RStruc;
import cho.carbon.meta.struc.er.Struc;
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
			Object userPrinciple, RecordEntity entity) {
		super(config, getContext(config, relationName, entity, userPrinciple), fieldMap, userPrinciple);
		Assert.hasText(relationName);
		this.relationName = relationName;
	}
	
	private static EntityBindContext getContext(FusionContextConfig config, String relationName, RecordEntity entity, Object user) {
		HCFusionContext c = config.getCurrentContext(user);
		Struc rootNode = c.getStruc();
		RStruc relNode = rootNode.findRStruc(relationName);
		return new ABCNodeEntityBindContext(relNode.getPointStruc(), entity);
	}

	@Override
	public Object getProperty(String propertyName, AttributeValueType propType) {
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
