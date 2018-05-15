package cn.sowell.datacenter.entityResolver;

import com.abc.mapping.entity.SimpleEntity;

public interface EntityProxy {

	void putValue(String propName, Object val);

	default Object getTypeValue(String propName, String abctype) {
		return getEntity().getTypeValue(propName, abctype);
	}
	
	SimpleEntity getEntity();

	EntityProxy createEmptyEntity();

	default boolean preprocessValue(String propName, Object propValue) {
		return true;
	}
}
