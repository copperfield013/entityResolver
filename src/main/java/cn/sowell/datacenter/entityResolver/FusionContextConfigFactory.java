package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.ModuleConfigStructure;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

public interface FusionContextConfigFactory {
	
	/**
	 * 根据配置的module的name获得对应的配置
	 * @param configId
	 * @return
	 */
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

	Set<FusionContextConfig> getAllConfigsLoaded();

	ModuleConfigStructure getConfigStructure(String moduleName);

}
