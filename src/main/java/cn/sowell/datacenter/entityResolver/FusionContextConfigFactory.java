package cn.sowell.datacenter.entityResolver;

import java.util.Map;
import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.ImportComposite;
import cn.sowell.datacenter.entityResolver.config.ModuleMeta;

public interface FusionContextConfigFactory {
	FusionContextConfig getConfig(String configId);
	/**
	 * 根据配置的mappingName获得对应的配置对象
	 * 因为mappingName并不是全局唯一的，所以可能会有多个，这里的只获取配置里的第一个
	 * @param mappingName
	 * @return
	 */
	FusionContextConfig getFirstConfigByMappingName(String mappingName);
	Set<FusionContextConfig> getConfigsByModule(String module);
	
	String getDefaultConfigId(String module);
	FusionContextConfig getDefaultConfig(String module);
	
	/**
	 * 根据配置的configId获得对应的解析器
	 * @param configId
	 * @return
	 */
	FusionContextConfigResolver getResolver(String configId);
	
	/**
	 * 根据配置获得module对应的默认解析器
	 * @param module
	 * @return
	 */
	FusionContextConfigResolver getModuleDefaultResolver(String module);
	/*
	 * 获得模块的元数据
	 */
	ModuleMeta getModuleMeta(String module);
	/**
	 * 根据模块名获得对应的所有的导入选项
	 * @param module
	 * @return
	 */
	Map<String, ImportComposite> getModuleImportMap(String module);
	FusionContextConfig getConfigDependented(String configId);
	/**
	 * 获得所有模块默认的配置
	 * @return
	 */
	Set<FusionContextConfig> getAllDefaultConfig();
	
}
