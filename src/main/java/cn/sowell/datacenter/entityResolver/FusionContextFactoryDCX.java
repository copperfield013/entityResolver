package cn.sowell.datacenter.entityResolver;

import java.util.HashMap;
import java.util.Map;

import com.abc.application.BizFusionContext;

public class FusionContextFactoryDCX {
	public static final String KEY_BASE = "base";
	public static final String KEY_IMPORT_BASE = "importBase";
	public static final String KEY_IMPORT_HANDICAPPED = "importHandicapped";
	public static final String KEY_IMPORT_LOWINCOME = "importLowincome";
	public static final String KEY_IMPORT_FAMILYPLANNING = "importFamilyPlanning";
	public static final String KEY_ADDRESS_BASE = "addressBase";
	public static final String KEY_STUDENT_BASE = "studentpartyBase";
	public static final String KEY_DISABLEDPEOPLE_BASE = "disabledpeople";
	public static final String KEY_HSPEOPLE_BASE = "hspeople";
	
	private Map<String, FusionContextConfig> configMap = new HashMap<String, FusionContextConfig>();
	
	private Map<String, String> defaultModuleEntityConfigMap = new HashMap<String, String>();
	
	public FusionContextFactoryDCX() {
		
		defaultModuleEntityConfigMap.put("people", KEY_BASE);
		/*defaultModuleEntityConfigMap.put(DataCenterConstants.MODULE_KEY_ADDRESS, KEY_ADDRESS_BASE);
		defaultModuleEntityConfigMap.put(DataCenterConstants.MODULE_KEY_STUDENT, KEY_STUDENT_BASE);
		defaultModuleEntityConfigMap.put(DataCenterConstants.MODULE_KEY_DISABLEDPEOPLE, KEY_DISABLEDPEOPLE_BASE);
		defaultModuleEntityConfigMap.put(DataCenterConstants.MODULE_KEY_HSPEOPLE, KEY_HSPEOPLE_BASE);*/
	}
	
	public FusionContextConfig getConfig(String configName){
		return configMap.get(configName);
	}
	
	public BizFusionContext getContext(String configName){
		if(configMap.containsKey(configName)){
			return getContext(configMap.get(configName));
		}else{
			throw new RuntimeException("没有配置FusitionContextConfig[name=" + configName + "]");
		}
	}
	
	public BizFusionContext getContext(FusionContextConfig config){
		BizFusionContext context = new BizFusionContext();
		context.setMappingName(config.getMappingName());
		return context;
	}
	public Map<String, FusionContextConfig> getConfigMap() {
		return configMap;
	}
	public void setConfigMap(Map<String, FusionContextConfig> configMap) {
		this.configMap = configMap;
	}

	public String mapDefaultModuleEntityConfig(String module) {
		return defaultModuleEntityConfigMap.get(module);
	}

	public FusionContextConfig getDefaultConfig(String module) {
		return getConfig(mapDefaultModuleEntityConfig(module));
	}
}
