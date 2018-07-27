package cn.sowell.datacenter.entityResolver;

import com.abc.util.ValueType;

import cn.sowell.datacenter.entityResolver.impl.PropertyValueBindReport;

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
	 * @return 
	 */
	PropertyValueBindReport setValue(String propName, Object propValue);
	
	/**
	 * 获得直接属性值
	 * @param propName
	 * @param abcAttr
	 * @return
	 */
	Object getValue(String propName, ValueType abcAttr);
	
	/**
	 * 获得直接子节点
	 * @param prefix
	 * @return
	 */
	EntityBindContext getElement(PropertyNamePartitions namePartitions);

	EntityProxy getEntity();
	
}
