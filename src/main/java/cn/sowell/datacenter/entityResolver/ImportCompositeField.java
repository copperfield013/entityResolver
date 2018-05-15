package cn.sowell.datacenter.entityResolver;

public interface ImportCompositeField {
	/**
	 * 
	 * @return
	 */
	String getFieldName();
	/**
	 * 
	 * @return
	 */
	boolean getIsMultipleField();
	/**
	 * 
	 * @return
	 */
	String getFieldNamePattern();
	
	String getRelationKey();
	
	String REPLACE_INDEX = "%INDEX%";
	
}
