package cn.sowell.datacenter.entityResolver;

import com.abc.mapping.entity.SimpleEntity;
import com.abc.util.ValueType;

public interface EntityProxy {

	void putValue(String propName, Object val);

	default Object getTypeValue(String propName, ValueType abctype) {
		return getEntity().getTypeValue(propName, abctype);
	}
	
	SimpleEntity getEntity();

	EntityProxy createEmptyEntity();

	default boolean preprocessValue(String propName, Object propValue) {
		return true;
	}
}
