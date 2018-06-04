package cn.sowell.datacenter.entityResolver.config;

class DBEntity extends TheEntity{
	private static final long serialVersionUID = -7626829828018016372L;
	private Long dbId;
	private Long moduleId;
	public Long getModuleId() {
		return moduleId;
	}
	void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public Long getDbId() {
		return dbId;
	}
	void setDbId(Long dbId) {
		this.dbId = dbId;
	}
}
