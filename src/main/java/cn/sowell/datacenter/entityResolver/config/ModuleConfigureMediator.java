package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.CreateModuleParam;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

/**
 * 用于处理模块配置数据的统一接口。
 * @author Copperfield
 * @date 2018年5月30日 上午10:01:18
 */
public interface ModuleConfigureMediator{
	/**
	 * 根据name获得模块。如果模块不存在，返回null
	 * @param moduleName 模块唯一名称
	 * @return
	 */
	Module getModule(String moduleName);
	
	/**
	 * 查询所有模块
	 * @return
	 */
	List<Module> queryModules();
	
	/**
	 * 根据条件查询模块
	 * @param param
	 * @return
	 */
	List<Module> queryModules(QueryModuleCriteria criteria);
	
	/**
	 * 创建模块
	 * @param moduleTitle
	 * @param defMappingName
	 * @param impTitle
	 */
	void createModule(String moduleTitle, String mappingName);
	/**
	 * 创建模块
	 * @param param
	 * @param moduleTitle	<b>模块标题，必须指定</b>
	 * @param mappingName 	<b>模块对应配置名，必须指定，而且必须已经在abc中配置</b>
	 * @param moduleName	模块名，需要全局唯一，不指定时会自动生成10位的随机码
	 * @param codeName		模块对应配置的编码字段名，不指定时会用”编码“来获取实体编码值
	 * @param titleName		模块对应配置的名称字段名，不指定时会用“姓名”来获取该名称字段值
	 */
	void createModule(CreateModuleParam param);
	
	/**
	 * 禁用模块
	 * 禁用模块之后，融合中心的系统中无法获取到该模块的配置
		但是调用接口的queryModule方法依然可以获取到该模块，
		调用{@link #enableModule(String)} 方法可以重新启用该模块
	 * @param moduleName
	 */
	void disableModule(String moduleName);
	/**
	 * 启用被禁用的模块
	 * @param moduleName
	 */
	void enableModule(String moduleName);
	
	/**
	 * <strong>(慎用该方法)</strong><br/>
	 * 移除模块
	 * 移除模块之后，无法再次查询到该模块的配置， <strong>无法还原</strong>
	 * @param moduleName
	 */
	void removeModule(String moduleName);
	
	/**
	 * 重新指定模块对应的配置
	 * 该方法不修改实体读取编码的字段名和名称字段名
	 * 如果要修改编码字段和名称字段名，请调用方法{@link #reassignMappingName(String, String, String, String)}
	 * @param entityId
	 * @param mappingName
	 */
	void reassignMappingName(String moduleName, String mappingName);
	
	/**
	 * 重新指定模块对应的配置，并且修改实体的编码字段和名称字段
	 * @param moduleName
	 * @param mappingName
	 * @param codeName
	 * @param titleName
	 */
	void reassignMappingName(String moduleName, String mappingName, String codeName, String titleName);

	/**
	 * 更新模块对应的配置中的编码字段和名称字段
	 * @param moduleName
	 * @param codeName
	 * @param titleName
	 */
	void updateModulePropertyName(String moduleName, String codeName, String titleName);

	void updateModuleCodeName(String moduleName, String codeName);

	void updateModuleTitleName(String moduleName, String titleName);
	
	
	
	
}
