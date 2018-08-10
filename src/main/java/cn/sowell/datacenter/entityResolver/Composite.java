package cn.sowell.datacenter.entityResolver;

public interface Composite {

	Object getIsArray();

	String getName();
	
	Integer getAddType();
	
	final Integer RELATION_ADD_TYPE = 5; 

}
