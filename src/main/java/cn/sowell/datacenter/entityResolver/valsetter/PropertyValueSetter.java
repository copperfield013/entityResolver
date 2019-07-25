package cn.sowell.datacenter.entityResolver.valsetter;

import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.datacenter.entityResolver.EntityProxy;
import cn.sowell.datacenter.entityResolver.impl.CommonPropertyValueBindReport;

public interface PropertyValueSetter {
	
	boolean support(AttributeValueType dataType, Object val);

	void invoke(EntityProxy entity, String propName, Object val, CommonPropertyValueBindReport report);


}
