package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import cn.sowell.datacenter.entityResolver.config.abst.Composite;
import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

public interface ModuleConfigDao {

	Module getModule(String moduleName);

	List<Module> queryModules(QueryModuleCriteria criteria);

	Long createModule(TheModule module);

	Long addEntity(DBEntity entity);

	Long getModuleId(String moduleName);

	void enableModule(Long moduleId, boolean toEnable);

	void removeModule(Long moduleId);

	Entity getEntity(String entityId);

	void reassignMappingName(String entityId, String mappingName);

	void reassignMappingName(String entityId, String mappingName, String codeName, String titleName);
	
	int removeEntity(String entityId);

	/**
	 * 获得entityId所在的module的默认entity
	 * @param entityId
	 * @return
	 */
	Entity getDefaultEntityOfEntitySiblings(String entityId);
	
	void changeEntityDefaultStatus(String entityId, boolean asDefault);

	Composite getImportComposite(String entityId);

	void addImportComposite(TheComposite composite);

	int retitleImportComposite(String entityId, String impTitle);

	int removeImportComposite(String entityId);

	Long getEntityId(String entityId);

	

	
}
