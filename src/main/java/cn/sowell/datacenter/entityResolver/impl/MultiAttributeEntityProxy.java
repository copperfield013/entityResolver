package cn.sowell.datacenter.entityResolver.impl;

import cho.carbon.entity.entity.LeafEntity;
import cn.sowell.datacenter.entityResolver.EntityProxy;

public class MultiAttributeEntityProxy implements EntityProxy{

	private LeafEntity entity;
	
	public MultiAttributeEntityProxy(LeafEntity entity) {
		super();
		this.entity = entity;
	}


	@Override
	public void putValue(String propName, Object val) {
		entity.putValue(propName, val);
	}
	
	@Override
	public EntityProxy createEmptyEntity() {
		LeafEntity copy = new LeafEntity(entity.getName());
		MultiAttributeEntityProxy proxy = new MultiAttributeEntityProxy(copy);
		return proxy;
	}


	@Override
	public LeafEntity getEntity() {
		return this.entity;
	}
}
