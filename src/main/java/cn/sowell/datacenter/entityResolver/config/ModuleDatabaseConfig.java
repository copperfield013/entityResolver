package cn.sowell.datacenter.entityResolver.config;

enum ModuleDatabaseConfig {
	TABLE_MODULE("t_config_module"),
	COLUMN_MODULE_ID("id"),
	COLUMN_MODULE_NAME("c_name"),
	COLUMN_MODULE_TITLE("c_title"),
	COLUMN_MODULE_DISABLED("c_disabled"),
	
	TABLE_MODULE_ENTITY("t_config_module_entity"),
	COLUMN_MODULE_ENTITY_ID("id"),
	COLUMN_MODULE_ENTITY_ENTITY_ID("c_entity_id"),
	COLUMN_MODULE_ENTITY_MAPPING_NAME("c_mapping_name"),
	COLUMN_MODULE_ENTITY_DEFAULT("c_default"),
	COLUMN_MODULE_ENTITY_CODE_NAME("c_code_name"),
	COLUMN_MODULE_ENTITY_TITLE_NAME("c_title_name"),
	COLUMN_MODULE_ENTITY_MODULE_ID("module_id"),
	
	TABLE_MODULE_IMPORT_COMPOSITE("t_config_module_import_composite"),
	COLUMN_MODULE_IMPORT_COMPOSITE_ID("id"),
	COLUMN_MODULE_IMPORT_COMPOSITE_NAME("c_name"),
	COLUMN_MODULE_IMPORT_COMPOSITE_TITLE("c_title"),
	COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID("entity_id"),
	
	
	TABLE_MODULE_FUNCTION("t_config_module_function"),
	COLUMN_MODULE_FUNCTION_ID("id"),
	COLUMN_MODULE_FUNCTION_NAME("c_name"),
	COLUMN_MODULE_FUNCTION_MODULE_ID("module_id"),
	COLUMN_MODULE_FUNCTION_ARGUMENTS("c_arguments")
	
	;
	
	private String value;
	private ModuleDatabaseConfig(String value) {
		this.value = value;
	}
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.getValue();
	}
}
