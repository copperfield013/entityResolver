package cn.sowell.datacenter.entityResolver;

public interface FieldConfigure {
	public static final String ATTRIBUTE_FILED_TYPE = "attribute";
	public static final String LABEL_FILED_TYPE = "label";
	public static final String MULTIATTR_FILED_TYPE = "multiattribute";
	public static final String RELATION_FILED_TYPE = "relation";
	/**
	 * 当前字段的name，即绝对路径的最后一部分
	 * @return
	 */
	String getThisName();
	/**
	 * 当前字段的父节点的name，即绝对路径的倒数第二部分。
	 * 如果绝对路径只有一段，那么返回null
	 * @return
	 */
	String getParentName();
	
	/**
	 * 字段的绝对路径
	 * @return
	 */
	String getAbsoluteName();
	/**
	 * 字段的类型，包括<ul>
	 * <li>{@link #ATTRIBUTE_FILED_TYPE}</li>
	 * <li>{@link #LABEL_FILED_TYPE}</li>
	 * <li>{@link #MULTIATTR_FILED_TYPE}</li>
	 * <li>{@link #RELATION_FILED_TYPE}</li>
	 * </ul>
	 * @return
	 */
	String getFieldType();
	/**
	 * 字段的配置类型
	 * @return
	 */
	String getAbcAttr();
	/**
	 * 配置主键
	 * @return
	 */
	Integer getMappingId();
}
