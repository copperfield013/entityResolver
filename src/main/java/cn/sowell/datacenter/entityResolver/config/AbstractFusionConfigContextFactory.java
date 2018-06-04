package cn.sowell.datacenter.entityResolver.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigImpl;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.config.abst.Composite;
import cn.sowell.datacenter.entityResolver.config.abst.Config;
import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Function;
import cn.sowell.datacenter.entityResolver.config.abst.Functions;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

public abstract class AbstractFusionConfigContextFactory implements FusionContextConfigFactory{
	private Map<String, FusionContextConfig> configMap = new HashMap<String, FusionContextConfig>();
	private Map<String, String> defaultModuleEntityConfigMap = new HashMap<String, String>();
	private Map<String, ModuleMeta> moduleMetaMap = new HashMap<String, ModuleMeta>();
	private Map<String, Map<String, ImportComposite>> importMap = new HashMap<String, Map<String, ImportComposite>>();
	
	protected AbstractFusionConfigContextFactory(Config config) throws FusionConfigException {
		for (Module module : config.getModules()) {
			if(moduleMetaMap.containsKey(module.getName())) {
				throwException("module的name[" + module.getName() + "]重复");
			}
			if(module.getEntities() != null) {
				for (Entity entity : module.getEntities()) {
					if(configMap.containsKey(entity.getId())) {
						throwException("存在重复id[" + entity.getId() + "]的entity");
					}
					if(entity.isDefault()) {
						if(defaultModuleEntityConfigMap.containsKey(module.getName())) {
							throwException("同一个module内，只能有一个entity为默认entity");
						}
						defaultModuleEntityConfigMap.put(module.getName(), entity.getId());
					}
					FusionContextConfigImpl cConfig = new FusionContextConfigImpl();
					cConfig.setModule(module.getName());
					cConfig.setMappingName(entity.getMappingName());
					if(TextUtils.hasText(entity.getCodeName())) {
						cConfig.setCodeAttributeName(entity.getCodeName());
					}
					if(TextUtils.hasText(entity.getTitleName())) {
						cConfig.setTitleAttributeName(entity.getTitleName());
					}
					configMap.put(entity.getId(), cConfig);
				}
			}
			if(module.getImport() != null) {
				Map<String, ImportComposite> mImpMap = new LinkedHashMap<String, ImportComposite>();
				if(module.getImport().getComposites() != null) {
					for (Composite composite : module.getImport().getComposites()) {
						ImportCompositeImpl iComposite = new ImportCompositeImpl();
						if(TextUtils.hasText(composite.getEntityId())) {
							iComposite.setConfigId(composite.getEntityId());
						}else {
							throw new UnconfiuredFusionException("composite必须指定entityId");
						}
						if(TextUtils.hasText(composite.getName())) {
							iComposite.setName(composite.getName());
						}else {
							throw new UnconfiuredFusionException("composite必须指定name");
						}
						if(TextUtils.hasText(composite.getTitle())) {
							iComposite.setTitle(composite.getTitle());
						}else {
							throw new UnconfiuredFusionException("composite必须指定title");
						}
						iComposite.setModule(module.getName());
						mImpMap.put(iComposite.getName(), iComposite);
					}
				}
				importMap.put(module.getName(), mImpMap);
			}
			if(!defaultModuleEntityConfigMap.containsKey(module.getName())) {
				throwException("module[name=" + module.getName() + "]内必须设置一个默认的entity");
			}
			ModuleMetaImpl meta = new ModuleMetaImpl(module.getName(), module.getTitle());
			Functions functions = module.getFunctions();
			if(functions != null) {
				if(functions.getFunctions() != null) {
					for (Function function : functions.getFunctions()) {
						meta.addFunction(function.getName());
					}
				}
			}
			moduleMetaMap.put(module.getName(), meta);
		}
	}
	
	
	protected abstract Set<FieldParserDescription> getFields(String module);


	private void throwException(String msg) throws FusionConfigException {
		throw new FusionConfigException(msg);
	}


	@Override
	public FusionContextConfig getConfig(String configId) {
		FusionContextConfig config = configMap.get(configId);
		if(config == null) {
			throw new UnconfiuredFusionException("没有配置id为[" + configId + "]的entity");
		}
		if(!config.hasLoadResolverFields()) {
			config.loadResolver(getFields(config.getModule()));
		}
		return config;
	}
	
	@Override
	public FusionContextConfig getConfigDependented(String configId) {
		FusionContextConfig config = configMap.get(configId);
		if(config == null) {
			throw new UnconfiuredFusionException("没有配置id为[" + configId + "]的entity");
		}
		return config;
	}

	@Override
	public FusionContextConfig getFirstConfigByMappingName(String mappingName)
	{
		FusionContextConfig config = configMap.values().stream().filter(c->c.getMappingName().equals(mappingName)).findFirst().get();
		if(config == null) {
			throw new UnconfiuredFusionException("没有配置mappingName为[" + mappingName + "]的entity");
		}
		if(!config.hasLoadResolverFields()) {
			config.loadResolver(getFields(config.getModule()));
		}
		return config;
	}

	@Override
	public Set<FusionContextConfig> getConfigsByModule(String module) {
		Set<FusionContextConfig> configs = configMap.values()
					.stream()
					.filter(config->config.getModule().equals(module))
					.collect(Collectors.toSet());
		if(configs == null || configs.size() == 0) {
			throw new UnconfiuredFusionException("没有配置属于模块[" + module + "]的entity");
		}
		for (FusionContextConfig config : configs) {
			if(!config.hasLoadResolverFields()) {
				config.loadResolver(getFields(config.getModule()));
			}
		}
		return configs;
	}

	@Override
	public String getDefaultConfigId(String module) {
		String configId = defaultModuleEntityConfigMap.get(module);
		if(configId == null) {
			throw new UnconfiuredFusionException("模块[" + module + "]的没有配置默认的entity");
		}
		return configId;
	}

	@Override
	public FusionContextConfig getDefaultConfig(String module) {
		return getConfig(getDefaultConfigId(module));
	}
	
	@Override
	public FusionContextConfigResolver getResolver(String configId) {
		FusionContextConfig config = getConfig(configId);
		if(config != null) {
			return config.getConfigResolver();
		}
		return null;
	}
	
	@Override
	public FusionContextConfigResolver getModuleDefaultResolver(String module) {
		FusionContextConfig defaultConfig = getDefaultConfig(module);
		if(defaultConfig != null) {
			return defaultConfig.getConfigResolver();
		}
		return null;
	}
	
	@Override
	public ModuleMeta getModuleMeta(String module) {
		ModuleMeta moduleMeta = moduleMetaMap.get(module);
		if(moduleMeta == null) {
			throw new UnconfiuredFusionException("没有配置模块[" + module + "]");
		}
		return moduleMeta;
	}

	
	@Override
	public Map<String, ImportComposite> getModuleImportMap(String module) {
		Map<String, ImportComposite> map = importMap.get(module);
		if(map == null) {
			throw new UnconfiuredFusionException("没有配置module[name=" + module + "的模块");
		}
		return new LinkedHashMap<String, ImportComposite>(map);
	}

	
	@Override
	public Set<FusionContextConfig> getAllDefaultConfig() {
		HashSet<FusionContextConfig> configs = new HashSet<>();
		defaultModuleEntityConfigMap.values().forEach(configId->{
			configs.add(getConfig(configId));
		});
		return configs;
	}

}

