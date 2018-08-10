package cn.sowell.datacenter.entityResolver.config;

enum ModuleDatabaseConfig {
	TABLE_MODULE("t_sb_config_module")
	,COLUMN_MODULE_ID("id")
	,COLUMN_MODULE_NAME("c_name")
	,COLUMN_MODULE_TITLE("c_title")
	,COLUMN_MODULE_DISABLED("c_disabled")
	,COLUMN_MODULE_MAPPING_NAME("c_mapping_name")
	,COLUMN_MODULE_CODE_NAME("c_code_name")
	,COLUMN_MODULE_TITLE_NAME("c_title_name")
	,COLUMN_MODULE_CREATE_TIME("create_time")
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
