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

	void reassignMappingName(String entityId, String mappingName);

	void reassignMappingName(String entityId, String mappingName, String codeName, String titleName);
	
}
