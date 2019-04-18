package cn.sowell.datacenter.entityResolver;

import java.util.Map;

import com.abc.model.enun.ValueType;

public interface CEntityPropertyParser {
	
	String getCode();
	/**
	 * 
	 * @return
	 */
	Map<String, String> getSmap();
	
	/**
	 * 使用内置的策略获得
	 * @return
	 */
	Map<String, Object> getPmap();
	
	/**
	 * 使用内置的策略获得对应的字段值
	 * @param propertyName 字段名（可为复合）
	 * @return
	 */
	Object getProperty(String propertyName);

	/**
	 * 指定数据类型获得对应的字段值对象
	 * @param propertyName 字段名（可为复合）
	 * @param propType
	 * @return
	 */
	Object getProperty(String propertyName, ValueType propType);

	/**
	 * 获得格式化后的字段文本
	 * @param propertyName 字段名
	 * @param propType 字段类型
	 * @param format 字段格式
	 * @return
	 */
	String getFormatedProperty(String propertyName, ValueType propType, String format);
	
	/**
	 * 获得格式化后的字段文本
	 * @param propertyName
	 * @param propType
	 * @return
	 */
	String getFormatedProperty(String propertyName, ValueType propType);
	
	/**
	 * 获得格式化后的字段文本
	 * @param propertyName
	 * @return
	 */
	String getFormatedProperty(String propertyName);
}
