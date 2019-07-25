package cn.sowell.datacenter.entityResolver;

import cho.carbon.entity.entity.LeafEntity;
import cho.carbon.meta.enun.AttributeValueType;

public interface EntityProxy {

	void putValue(String propName, Object val);

	default Object getTypeValue(String propName, AttributeValueType abctype) {
		return getEntity().getTypeValue(propName, abctype);
	}
	
	LeafEntity getEntity();

	EntityProxy createEmptyEntity();

	default boolean preprocessValue(String propName, Object propValue) {
		return true;
	}
}
