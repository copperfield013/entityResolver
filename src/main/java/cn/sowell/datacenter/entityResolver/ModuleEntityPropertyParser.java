package cn.sowell.datacenter.entityResolver;

import java.util.List;
import java.util.Map;

import cn.sowell.datacenter.entityResolver.impl.ArrayItemPropertyParser;

public interface ModuleEntityPropertyParser extends CEntityPropertyParser{
	
	String getId();

	String getTitle();
	
	Map<String, List<ArrayItemPropertyParser>> getArrayMap();

	
//	List<ErrorInfomation> getErrors();
//
//	void setErrors(List<ErrorInfomation> errors);


	List<ArrayItemPropertyParser> getCompositeArray(String compositeName);


}
