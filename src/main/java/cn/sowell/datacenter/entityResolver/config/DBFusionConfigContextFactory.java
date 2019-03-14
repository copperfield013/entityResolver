package cn.sowell.datacenter.entityResolver.config;

import java.util.Set;

import org.hibernate.SessionFactory;

import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.UserCodeService;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

public class DBFusionConfigContextFactory implements FusionContextConfigFactory{

	private CommonFusionConfigContextFactory fFactory;
	private boolean syncFlag = true;
	private ModuleConfigBuilder configBuilder;
	private FieldService fieldService;
	private UserCodeService userCodeService;
	
	
	public synchronized void setConfigBuilder(ModuleConfigBuilder configBuilder) {
		this.configBuilder = configBuilder;
	}
	
	public synchronized void setSessionFactory(SessionFactory sessionFactory) {
		DatabaseQuery databaseConfig = new DatabaseQuery();
		DatabseModuleConfigBuilder builder = new DatabseModuleConfigBuilder(databaseConfig, sessionFactory);
		setConfigBuilder(builder);
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
		fFactory = new CommonFusionConfigContextFactory(configBuilder.getConfig());
		if(this.fieldService != null) {
			fFactory.setFieldsService(this.fieldService);
		}
		if(this.userCodeService != null) {
			fFactory.setUserCodeService(this.userCodeService);
		}
	}

	public synchronized void sync() {
		syncFlag = true;
	}

	@Override
	public FusionContextConfig getModuleConfig(String moduleName) {
		return getFactory().getModuleConfig(moduleName);
	}
	
	@Override
	public FusionContextConfig getModuleConfigDependended(String moduleName) {
		return getFactory().getModuleConfigDependended(moduleName);
	}
	
	@Override
	public FusionContextConfigResolver getModuleResolver(String moduleName) {
		return getFactory().getModuleResolver(moduleName);
	}

	@Override
	public Module getModule(String moduleName) {
		return getFactory().getModule(moduleName);
	}

	@Override
	public Set<FusionContextConfig> getAllConfigs() {
		return getFactory().getAllConfigs();
	}
	
	@Override
	public Set<FusionContextConfig> getAllConfigsLoaded() {
		return getFactory().getAllConfigsLoaded();
	}

	public void setUserCodeService(UserCodeService userCodeService) {
		this.userCodeService = userCodeService;
	}
	
	@Override
	public ModuleConfigStructure getConfigStructure(String moduleName) {
		return getFactory().getConfigStructure(moduleName);
	}
	

}
