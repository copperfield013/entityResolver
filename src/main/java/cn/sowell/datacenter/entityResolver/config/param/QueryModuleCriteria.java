package cn.sowell.datacenter.entityResolver.config.param;

import java.io.Serializable;

public class QueryModuleCriteria implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7643790381638997660L;
	private Long moduleId;
	private String moduleName;
	private boolean filterDisabled = false;
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public boolean isFilterDisabled() {
		return filterDisabled;
	}

	public void setFilterDisabled(boolean filterDisabled) {
		this.filterDisabled = filterDisabled;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

}
