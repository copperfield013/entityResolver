package cn.sowell.datacenter.entityResolver;
/**
 * 
 * @author Copperfield
 * @date 2018年3月20日 下午3:39:40
 */
public interface EntityBindContext {

	/**
	 * 设置直接属性值
	 * @param propName
	 * @param propValue
	 */
	void setValue(String propName, Object propValue);
	
	/**
	 * 获得直接属性值
	 * @param propName
	 * @param abcAttr
	 * @return
	 */
	Object getValue(String propName, String abcAttr);
	
	/**
	 * 获得直接子节点
	 * @param prefix
	 * @return
	 */
	EntityBindContext getElement(PropertyNamePartitions namePartitions);

	EntityProxy getEntity();
	
}
