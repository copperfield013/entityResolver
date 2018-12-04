package cn.sowell.datacenter.entityResolver.config.abst;

import java.io.Serializable;

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
	 * abcnode配置主键
	 * @return
	 */
	Long getMappingId();
	
	/**
	 * 配置中获得实体编码的字段名
	 * @return
	 */
	String getCodeName();
	
	/**
	 * 配置中获得实体名称的字段名
	 * @return
	 */
	String getTitleName();
	
	/**
	 * 模块是否被禁用
	 * @return
	 */
	boolean isDisabled();
	
	
}
