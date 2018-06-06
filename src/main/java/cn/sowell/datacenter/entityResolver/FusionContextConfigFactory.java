package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.abst.Module;

public interface FusionContextConfigFactory {
	/**
	 * 根据配置的mappingName获得对应的配置对象
	 * 因为mappingName并不是全局唯一的，所以可能会有多个，这里的只获取配置里的第一个
	 * @param mappingName
	 * @return
	 */
	//FusionContextConfig getFirstConfigByMappingName(String mappingName);
	
	FusionContextConfig getModuleConfig(String moduleName);
	
	/**
	 * 根据配置的module的name获得对应的解析器
	 * @param configId
	 * @return
	 */
	FusionContextConfigResolver getModuleResolver(String moduleName);
	
	/*
	 * 获得模块的元数据
	 */
	Module getModule(String moduleName);

	Set<FusionContextConfig> getAllConfigs();

	FusionContextConfig getModuleConfigDependended(String moduleName);

}
