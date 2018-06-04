package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.AddEntityParam;
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
	 * 创建模块。用该方式创建的模块，会自动生成默认实体，但不会在导入功能中生成条线选项
	 * @param moduleTitle
	 * @param defMappingName
	 */
	void createModule(String moduleTitle, String defMappingName);
	/**
	 * 创建模块。用该方式创建的模块，会自动生成默认实体，同时在对应的导入功能中创建一个impTitle的选项
	 * @param moduleTitle
	 * @param defMappingName
	 * @param impTitle
	 */
	void createModule(String moduleTitle, String defMappingName, String impTitle);
	/**
	 * 创建模块
	 * @param param
	 * @param moduleTitle	 <b>模块标题，必须指定</b>
	 * @param defMappingName <b>模块默认的实体的对应配置名，必须指定，而且必须已经在abc中配置</b>
	 * @param moduleName	  模块名，需要全局唯一，不指定时会自动生成10位的随机码
	 * @param defEntityId	  默认模块的实体的id，需要全局唯一，不指定时会自动生成10位随机码
	 * @param defCodeName	  模块默认实体的的编码字段名，不指定时会用”编码“来获取实体编码值
	 * @param defTitleName	  模块默认实体的名称字段名，不指定时会用“姓名”来获取该名称字段值
	 * @param defForImport	  模块默认实体是否用来导入，默认为false
	 * @param impTitle		  用于显示在导入功能中作为导入条线的选项
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
	 * 
	 * @param entityId
	 * @return
	 */
	Entity getEntity(String entityId);
	
	/**
	 * 添加模块实体
	 * @param moduleName  模块名，必须指定
	 * @param mappingName 添加的实体的对应配置名，必须指定，而且必须已经在abc中配置
	 */
	void addModuleEntity(String moduleName, String mappingName);
	/**
	 * 添加模块实体
	 * 
	 * @param param
	 * @param moduleName	: <b>模块名，必须指定</b>
	 * @param mappingName	: <b>添加的实体的对应配置名，必须指定，而且必须已经在abc中配置</b>
	 * @param entityId		：添加的实体id，需要全局唯一，不指定时会自动生成10位随机码
	 * @param codeName		: 添加的实体的的编码字段名，不指定时会用”编码“来获取实体编码值
	 * @param titleName		: 添加的实体的名称字段名，不指定时会用“姓名”来获取该名称字段值
	 * @param forImport		: 添加的实体是否用于导入，默认为false
	 * @param impTitle		: 当forImport不设置或者为true时必须指定，用于显示在导入功能中作为导入条线的选项
	 */
	void addModuleEntity(AddEntityParam param);
	
	/**
	 * 重新指定实体对应的配置
	 * 该方法不修改实体读取编码的字段名和名称字段名
	 * 如果要修改编码字段和名称字段名，请调用方法{@link #reassignMappingName(String, String, String, String)}
	 * @param entityId
	 * @param mappingName
	 */
	void reassignMappingName(String entityId, String mappingName);
	
	/**
	 * 重新指定实体对应的配置，并且修改实体的编码字段和名称字段
	 * @param entityId
	 * @param mappingName
	 * @param codeName
	 * @param titleName
	 */
	void reassignMappingName(String entityId, String mappingName, String codeName, String titleName);
	
	/**
	 * 移除模块实体
	 * @param entityId
	 */
	void removeEntity(String entityId);
	
	/**
	 * 切换模块的默认实体（
	 * @param entityId
	 */
	void switchDefaultEntity(String entityId);
	
	/**
	 * 添加模块导入条线
	 * @param entityId 导入条线的实体id，必须是moduleName对应的模块内已经存在的实体，且（模块，实体）全局唯一
	 * @param impTitle 用于显示在导入功能中作为导入条线的选项，必须指定
	 */
	void addModuleImportComposite(String entityId, String impTitle);
	
	/**
	 * 重命名导入条线
	 * @param entityId
	 * @param impTitle
	 */
	void retitleModuleImport(String entityId, String impTitle);
	
	/**
	 * 移除模块导入条线
	 * @param entityId
	 */
	void removeModuleImport(String entityId);

	
	
	
	
}
