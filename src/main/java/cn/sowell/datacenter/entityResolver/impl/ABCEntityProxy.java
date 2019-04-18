package cn.sowell.datacenter.entityResolver.impl;

import com.abc.mapping.entity.Entity;
import com.abc.model.enun.ValueType;

import cn.sowell.datacenter.entityResolver.EntityProxy;

public class ABCEntityProxy extends EntitiesContainedEntityProxy {

	private Entity entity;
	
	public ABCEntityProxy(Entity entity) {
		super();
		this.entity = entity;
	}

	
	@Override
	public Object getTypeValue(String propName, ValueType abctype) {
		return super.getTypeValue(propName, abctype);
	}
	
	@Override
	public void putValue(String propName, Object val) {
		entity.putValue(propName, val);
	}

	@Override
	protected Entity getSourceEntity() {
		return entity;
	}

	@Override
	public EntityProxy createEmptyEntity() {
		Entity copy = new Entity(entity.getName());
		return new ABCEntityProxy(copy);
	}

}
