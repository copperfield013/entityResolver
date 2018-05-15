package cn.sowell.datacenter.entityResolver.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.RecordEntity;
import com.abc.mapping.entity.SimpleEntity;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.EntityProxy;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;

public abstract class EntitiesContainedEntityProxy implements EntityProxy{

	public void putEntity(PropertyNamePartitions namePartitions, EntityProxy entity) {
		Entity thisEntity = getSourceEntity();
		if(entity instanceof MultiAttributeEntityProxy) {
			List<SimpleEntity> multiAttrEntities = thisEntity.getMultiAttrEntity(namePartitions.getMainPartition());
			int multiattrSize = multiAttrEntities == null? 0: multiAttrEntities.size();
			if(multiattrSize <= namePartitions.getIndex()) {
				for(int i = multiattrSize; i < namePartitions.getIndex(); i++) {
					thisEntity.putMultiAttrEntity(entity.createEmptyEntity().getEntity());
				}
				thisEntity.putMultiAttrEntity(entity.getEntity());
			}
		}else if(entity instanceof RelationEntityProxy) {
			addRelationProxy(namePartitions, (RelationEntityProxy) entity);
		}
	}
	public void commit() {
		relationMap.forEach((propName, rels)->{
			rels.forEach(rel->{
				getSourceEntity().putRelationEntity(propName, rel.getLabel(), rel.getSourceEntity());
				rel.commit();
			});
		});
		relationMap.clear();
	}
	Map<String, List<RelationEntityProxy>> relationMap = new HashMap<>();
	private void addRelationProxy(PropertyNamePartitions namePartitions, RelationEntityProxy entity) {
		if(!relationMap.containsKey(namePartitions.getMainPartition())) {
			List<RelationEntityProxy> list = new ArrayList<>();
			relationMap.put(namePartitions.getMainPartition(), list);
		}
		List<RelationEntityProxy> relEntity = relationMap.get(namePartitions.getMainPartition());
		if(relEntity.size() <= namePartitions.getIndex()) {
			for(int i = relEntity.size(); i < namePartitions.getIndex(); i++) {
				relEntity.add((RelationEntityProxy)entity.createEmptyEntity());
			}
			relEntity.add(entity);
		}
	}

	//用于保存当前实体的子实体
	//因为如果子实体是relation，那么其可能还会包含子实体
	Map<String, EntityProxy> compositeMap = new HashMap<>();
	public List<EntityProxy> getEntityElements(String compositeName) {
		List<EntityProxy> entities = new ArrayList<>();
		List<RelationEntityProxy> relProxies = relationMap.get(compositeName);
		if(relProxies != null) {
			return CollectionUtils.toList(relProxies, proxy->proxy);
		}
		List<RecordEntity> rels = getSourceEntity().getRelations(compositeName);
		if(rels != null) {
			rels.forEach(rel->{
				EntityProxy existEntity = compositeMap.get(rel.getEntity().getId());
				if(existEntity != null) {
					entities.add(existEntity);
				}else {
					RelationEntityProxy relEntity = new RelationEntityProxy(rel.getEntity());
					relEntity.setLabel(rel.getLabelValue());
					compositeMap.put(rel.getEntity().getId(), relEntity);
					entities.add(relEntity);
				}
			});
			return entities;
		}else {
			List<SimpleEntity> multiAttrs = getSourceEntity().getMultiAttrEntity(compositeName);
			if(multiAttrs != null) {
				multiAttrs.forEach(multiAttr->entities.add(new MultiAttributeEntityProxy(multiAttr)));
				return entities;
			}
		}
		return null;
	}
	
	@Override
	public final SimpleEntity getEntity() {
		return getSourceEntity();
	}
	protected abstract Entity getSourceEntity();
}
