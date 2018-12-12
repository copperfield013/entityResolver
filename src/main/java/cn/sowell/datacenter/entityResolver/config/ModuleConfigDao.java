package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

public interface ModuleConfigDao {

	DBModule getModule(String moduleName);

	List<Module> queryModules(QueryModuleCriteria criteria);

	Long createModule(DBModule module);

	Long getModuleId(String moduleName);

	void enableModule(Long moduleId, boolean toEnable);

	void removeModule(Long moduleId);

	void reassignMappingId(String entityId, Long mappingId);

	void reassignMappingId(String entityId, Long mappingId, String codeName, String titleName);

	void updateModulePropertyName(String moduleName, String codeName, String titleName);

	void updateModuleCodeName(String moduleName, String codeName);

	void updateModuleTitleName(String moduleName, String titleName);

	List<Module> queryModules();

	void updateModule(String moduleName, String moduleTitle, String codeName, String titleName);

}
