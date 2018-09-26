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
	 * 获得直接子节点,如果子节点不存在，那么就创建一个
	 * @param prefix
	 * @return
	 */
	EntityBindContext getElementAutoCreate(PropertyNamePartitions namePartitions);
	
	/**
	 * 获得直接子节点，如果子节点不存在，那么返回空
	 * @param propName
	 * @return
	 */
	EntityBindContext getElementIfExists(PropertyNamePartitions propName);
	
	EntityProxy getEntity();

	void removeAllComposite(String compositeName);

	
	
}
