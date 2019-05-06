package cn.sowell.datacenter.entityResolver.impl;

import java.util.HashSet;
import java.util.Set;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.RecordEntity;
import com.abc.model.enun.ValueType;

import cn.sowell.copframe.utils.Assert;
import cn.sowell.datacenter.entityResolver.EntityConstants;
import cn.sowell.datacenter.entityResolver.EntityProxy;

public class RelationEntityProxy extends EntitiesContainedEntityProxy{
	private String label;
	private RecordEntity sourceEntity;
	
	public RelationEntityProxy(RecordEntity sourceEntity) {
		super();
		Assert.notNull(sourceEntity);
		this.sourceEntity = sourceEntity;
	}

	@Override
	public void putValue(String propName, Object val) {
		sourceEntity.putValue(propName, val);
	}

	@Override
	protected RecordEntity getSourceEntity() {
		return sourceEntity;
	}
	
	Set<RelationEntityProxy> copyEntities = new HashSet<>();
	@Override
	public EntityProxy createEmptyEntity() {
		Entity entity = new Entity(sourceEntity.getName());
		RelationEntityProxy proxy = new RelationEntityProxy(entity);
		proxy.setLabel(null);
		copyEntities.add(proxy);
		return proxy;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		//copyEntities.forEach(proxy->proxy.setLabel(label));
	}

	@Override
	public boolean preprocessValue(String propName, Object propValue) {
		super.preprocessValue(propName, propValue);
		if(EntityConstants.LABEL_KEY.equals(propName)) {
			this.setLabel(String.valueOf(propValue));
			return false;
		}
		return super.preprocessValue(propName, propValue);
	}
	
	@Override
	public Object getTypeValue(String propName, ValueType abctype) {
		if(EntityConstants.LABEL_KEY.equals(propName)) {
			return getLabel();
		}else {
			return super.getTypeValue(propName, abctype);
		}
	}
	
}
