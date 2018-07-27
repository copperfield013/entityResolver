package cn.sowell.datacenter.entityResolver.impl;

import com.abc.util.ValueType;

import cn.sowell.datacenter.entityResolver.EntityElement;

public class EntityAttrElement extends EntityElement{
	private ValueType dataType;

	public ValueType getDataType() {
		return dataType;
	}

	public void setDataType(ValueType valueType) {
		this.dataType = valueType;
	}

}
