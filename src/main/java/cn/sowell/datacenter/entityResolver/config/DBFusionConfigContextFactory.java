package cn.sowell.datacenter.entityResolver.config;

import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;

import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;

public class DBFusionConfigContextFactory implements FusionContextConfigFactory{

	private CommonFusionConfigContextFactory fFactory;
	private boolean syncFlag = true;
	private ModuleConfigBuilder configDao;
	private FieldService fieldService;
	
	
	public synchronized void setConfigDao(ModuleConfigBuilder configDao) {
		this.configDao = configDao;
	}
	
	public synchronized void setSessionFactory(SessionFactory sessionFactory) {
		DatabaseQuery databaseConfig = new DatabaseQuery();
		DatabseModuleConfigBuilder proxy = new DatabseModuleConfigBuilder(databaseConfig, sessionFactory);
		setConfigDao(proxy);
	}
	
	public void setFieldService(FieldService fieldService) {
		this.fieldService = fieldService;
	}
	
	private synchronized FusionContextConfigFactory getFactory() {
		if(syncFlag) {
			try {
				syncFromDatabase();
				if(fFactory == null) {
					throw new NullPointerException("同步之后，FusionContextConfigFactory依然为null");
				}
				syncFlag = false;
			} catch (Exception e) {
				//TODO: 载入时发生异常的重载限制
				throw new RuntimeException("无法根据数据库中的模块配置生成FusionConfigContextFactory对象", e);
			}
		}
		return fFactory;
	}
	
	private void syncFromDatabase() throws FusionConfigException {
		fFactory = new CommonFusionConfigContextFactory(configDao.getConfig());
		if(fieldService != null) {
			fFactory.setFieldsGetter((module)->this.fieldService.getFieldDescriptions(module));
		}
	}

	public synchronized void sync() {
		syncFlag = true;
	}
	
	@Override
	public FusionContextConfig getConfig(String configId) {
		return getFactory().getConfig(configId);
	}

	@Override
	public FusionContextConfig getFirstConfigByMappingName(String mappingName) {
		return getFactory().getFirstConfigByMappingName(mappingName);
	}

	@Override
	public Set<FusionContextConfig> getConfigsByModule(String module) {
		return getFactory().getConfigsByModule(module);
	}

	@Override
	public String getDefaultConfigId(String module) {
		return getFactory().getDefaultConfigId(module);
	}

	@Override
	public FusionContextConfig getDefaultConfig(String module) {
		return getFactory().getDefaultConfig(module);
	}

	@Override
	public FusionContextConfigResolver getResolver(String configId) {
		return getFactory().getResolver(configId);
	}

	@Override
	public FusionContextConfigResolver getModuleDefaultResolver(String module) {
		return getFactory().getModuleDefaultResolver(module);
	}

	@Override
	public ModuleMeta getModuleMeta(String module) {
		return getFactory().getModuleMeta(module);
	}

	@Override
	public Map<String, ImportComposite> getModuleImportMap(String module) {
		return getFactory().getModuleImportMap(module);
	}

	@Override
	public FusionContextConfig getConfigDependented(String configId) {
		return getFactory().getConfigDependented(configId);
	}

	@Override
	public Set<FusionContextConfig> getAllDefaultConfig() {
		return getFactory().getAllDefaultConfig();
	}

}
