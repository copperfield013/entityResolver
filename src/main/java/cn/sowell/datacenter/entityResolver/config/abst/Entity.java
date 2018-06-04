package cn.sowell.datacenter.entityResolver.config.abst;

import java.io.Serializable;

/**
 * 模块的实体对象
 * 实体只依赖于模块存在。
 * @author Copperfield
 * @date 2018年6月3日 下午12:39:57
 */
public interface Entity extends Serializable{

	/**
	 * 模块唯一标识。全局唯一，不同的模块的实体id也不允许重复
	 * @return 该值不为空
	 */
	String getId();

	/**
	 * 实体对应的配置的名称
	 * @return 该值不为空
	 */
	String getMappingName();

	/**
	 * 实体在模块内是否是默认的实体
	 * @return
	 */
	boolean isDefault();

	/**
	 * 实体在获取实体code时的字段名称
	 * @return 该值可能范围空
	 */
	String getCodeName();

	
	/**
	 * 实体在获取实体名称时的字段名称。
	 * 例如人口的“姓名”字段在配置中的字段名为“名字”，那么这里将会返回值“名字”
	 * @return 该值可能返回空
	 */
	String getTitleName();

}