package cn.sowell.datacenter.entityResolver.impl;

import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.valsetter.PropertyValueSetter;

public interface PropertyValueBindReport {
	boolean isValueProcessed();

	EntityElement getPropertyEntityElement();

	boolean isEntityElementFiltered();

	PropertyValueSetter getSetter();

	boolean isValueAsNull();

	PropertyType getPropertyType();
	
	
	enum PropertyType{
		FILE,
		Undefined;
	}
}
