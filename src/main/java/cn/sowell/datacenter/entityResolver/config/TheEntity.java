package cn.sowell.datacenter.entityResolver.config;

import cn.sowell.datacenter.entityResolver.config.abst.Entity;

public class TheEntity implements Entity {
	private static final long serialVersionUID = 1754017231578902082L;
	private String id;
	private String mappingName;
	private boolean isDefault;
	private String codeName;
	private String titleName;
	@Override
	public String getId() {
		return id;
	}
	void setId(String id) {
		this.id = id;
	}
	@Override
	public String getMappingName() {
		return mappingName;
	}
	void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}
	@Override
	public boolean isDefault() {
		return isDefault;
	}
	void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	@Override
	public String getCodeName() {
		return codeName;
	}
	void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	@Override
	public String getTitleName() {
		return titleName;
	}
	void setTitleName(String titleName) {
		this.titleName = titleName;
	}
}
