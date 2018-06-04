package cn.sowell.datacenter.entityResolver.config.abst;

import java.io.Serializable;

/**
 * 导入条线信息接口
 * @author Copperfield
 * @date 2018年6月3日 下午12:45:48
 */
public interface Composite extends Serializable{

	/**
	 * 导入条线绑定的实体的id
	 * @return 该值不为空
	 */
	String getEntityId();

	/**
	 * 导入条线在导入时的选项名
	 * @return 该值不为空
	 */
	String getTitle();

	/**
	 * 导入条线的唯一标志，在模块内唯一
	 * @return 该值不为空
	 */
	String getName();

}