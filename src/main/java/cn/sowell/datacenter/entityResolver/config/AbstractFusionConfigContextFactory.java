package cn.sowell.datacenter.entityResolver.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigImpl;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.config.abst.Config;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

public abstract class AbstractFusionConfigContextFactory implements FusionContextConfigFactory{
	private Map<String, FusionContextConfig> configMap = new HashMap<String, FusionContextConfig>();
	private Map<String, Module> moduleMap = new HashMap<String, Module>();
	
	protected AbstractFusionConfigContextFactory(Config config) throws FusionConfigException {
		for (Module module : config.getModules()) {
			if(moduleMap.containsKey(module.getName())) {
				throwException("module的name[" + module.getName() + "]重复");
			}
			if(!TextUtils.hasText(module.getTitle())) {
				throwException("module[name=" + module.getTitle() + "]的title不能为空");
			}
			/*try {
				if(MappingContainer.getABCNode(module.getMappingName()) == null) {
					throwException("MappingContainer.getABCNode(module.getMappingName())返回null值");
				}
			} catch (Exception e) {
				throw new FusionConfigException("无法根据mappingName[" + module.getMappingName() + "]获得abcNode", e);
			}*/
			moduleMap.put(module.getName(), module);
			FusionContextConfigImpl cConfig = new FusionContextConfigImpl();
			cConfig.setModule(module.getName());
			cConfig.setMappingName(module.getMappingName());
			if(TextUtils.hasText(module.getCodeName())) {
				cConfig.setCodeAttributeName(module.getCodeName());
			}
			if(TextUtils.hasText(module.getTitleName())) {
				cConfig.setTitleAttributeName(module.getTitleName());
			}
			configMap.put(module.getName(), cConfig);
		}
	}
	
	
	protected abstract Set<FieldParserDescription> getFields(String module);


	private void throwException(String msg) throws FusionConfigException {
		throw new FusionConfigException(msg);
	}


	@Override
	public FusionContextConfig getModuleConfig(String moduleName) {
		FusionContextConfig config = getModuleConfigDependended(moduleName);
		if(!config.hasLoadResolverFields()) {
			config.loadResolver(getFields(config.getModule()));
		}
		return config;
	}
	
	@Override
	public FusionContextConfig getModuleConfigDependended(String moduleName) {
		FusionContextConfig config = configMap.get(moduleName);
		if(config == null) {
			throw new UnconfiuredFusionException("不存在module[" + moduleName + "]的FusionContextConfig");
		}
		return config;
	}
	

	
	@Override
	public FusionContextConfigResolver getModuleResolver(String moduleName) {
		FusionContextConfig config = getModuleConfig(moduleName);
		if(config != null) {
			return config.getConfigResolver();
		}
		return null;
	}
	
	@Override
	public Module getModule(String moduleName) {
		Module module = moduleMap.get(moduleName);
		if(module == null) {
			throw new UnconfiuredFusionException("没有配置模块[" + moduleName + "]"); 
		}
		return module;
	}

	@Override
	public Set<FusionContextConfig> getAllConfigs() {
		return new LinkedHashSet<FusionContextConfig>(configMap.values());
	}

}

