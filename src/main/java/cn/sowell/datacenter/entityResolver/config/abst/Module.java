package cn.sowell.datacenter.entityResolver.config.abst;

import java.io.Serializable;
import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.ModuleConfigureMediator;
import cn.sowell.datacenter.entityResolver.config.RemoteModuleConfigureMediator;

/**
 * 数据模块。该接口只允许读取数据。
 * 如果需要修改模块的配置，本地项目请调用中介对象{@link ModuleConfigureMediator}接口方法，
 * 远程项目请调用中介对象{@link RemoteModuleConfigureMediator}接口方法
 * @author Copperfield
 * @date 2018年6月3日 下午12:36:56
 */
public interface Module extends Serializable {
	/**
	 * 模块的name，全局唯一的标识
	 * @return
	 */
	String getName();

	/**
	 * 模块的标题
	 * @return
	 */
	String getTitle();

	/**
	 * 获得模块下的所有实体
	 * @return
	 */
	Set<Entity> getEntities();
	
	/**
	 * 获得模块的默认实体。<br/>
	 * 正常情况下，从系统获取的模块都有一个，且只有一个默认实体
	 * @return
	 */
	Entity getDefautEntity();

	/**
	 * 模块是否被禁用
	 * @return
	 */
	boolean isDisabled();
	
	/**
	 * 获得模块的导入配置
	 * @return
	 */
	Import getImport();

	/**
	 * 获得模块的功能配置（0.x版本下，该方法的值获取值不可靠）
	 * @return
	 */
	Functions getFunctions();
	
	
}
