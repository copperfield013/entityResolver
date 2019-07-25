package cn.sowell.datacenter.entityResolver.impl;

import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.datacenter.entityResolver.EntityElement;

public class EntityAttrElement extends EntityElement{
	private AttributeValueType dataType;

	public AttributeValueType getDataType() {
		return dataType;
	}

	public void setDataType(AttributeValueType valueType) {
		this.dataType = valueType;
	}

}
