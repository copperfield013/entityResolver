package cn.sowell.datacenter.entityResolver.impl;

import com.abc.mapping.entity.SimpleEntity;

import cn.sowell.datacenter.entityResolver.EntityProxy;

public class MultiAttributeEntityProxy implements EntityProxy{

	private SimpleEntity entity;
	
	public MultiAttributeEntityProxy(SimpleEntity entity) {
		super();
		this.entity = entity;
	}


	@Override
	public void putValue(String propName, Object val) {
		entity.putValue(propName, val);
	}
	
	@Override
	public EntityProxy createEmptyEntity() {
		SimpleEntity copy = new SimpleEntity(entity.getName());
		MultiAttributeEntityProxy proxy = new MultiAttributeEntityProxy(copy);
		return proxy;
	}


	@Override
	public SimpleEntity getEntity() {
		return this.entity;
	}
}
