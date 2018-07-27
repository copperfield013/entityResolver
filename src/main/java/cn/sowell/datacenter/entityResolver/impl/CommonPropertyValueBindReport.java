package cn.sowell.datacenter.entityResolver.impl;

import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.valsetter.PropertyValueSetter;

public class CommonPropertyValueBindReport implements PropertyValueBindReport{

	private boolean valueProcessed = false;
	private EntityElement propertyEntityElement;
	private boolean entityElementFiltered = false;
	private PropertyValueSetter setter;
	private boolean valueAsNull = false;
	private PropertyType propertyType = PropertyType.Undefined;
	
	protected void setValuePreprocessed(boolean valueProcessed) {
		this.valueProcessed = valueProcessed;
		
	}

	protected void setPropertyEntityElement(EntityElement eElement) {
		this.propertyEntityElement = eElement;
		
	}

	protected void setEntittyElementFiltered(boolean entityElementFiltered) {
		this.entityElementFiltered = entityElementFiltered;
		
	}

	protected void setPropertyValueSetter(PropertyValueSetter setter) {
		this.setter = setter;
		
	}

	public void setValueAsNull(boolean asNull) {
		this.valueAsNull = asNull;
		
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
		
	}

	@Override
	public boolean isValueProcessed() {
		return valueProcessed;
	}

	@Override
	public EntityElement getPropertyEntityElement() {
		return propertyEntityElement;
	}

	@Override
	public boolean isEntityElementFiltered() {
		return entityElementFiltered;
	}

	@Override
	public PropertyValueSetter getSetter() {
		return setter;
	}
	
	@Override
	public boolean isValueAsNull() {
		return valueAsNull;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

}
