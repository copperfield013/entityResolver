package cn.sowell.datacenter.entityResolver.config;

import cn.sowell.datacenter.entityResolver.config.abst.Module;

class TheModule implements Module {
	private static final long serialVersionUID = 1177752285709607977L;
	private String name;
	private String title;
	private Integer mappingId;
	private String codeName;
	private String titleName;
	private boolean disabled;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getMappingId() {
		return mappingId;
	}
	public void setMappingId(Integer mappingId) {
		this.mappingId = mappingId;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
