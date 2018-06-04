package cn.sowell.datacenter.entityResolver.config.param;

import java.io.Serializable;

public class AddEntityParam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1470989595666384978L;
	/**
	 * 模块名，必须指定
	 */
	private String moduleName;
	/**
	 * 添加的实体id，需要全局唯一，不指定时会自动生成10位随机码
	 */
	private String entityId;
	/**
	 * 添加的实体的对应配置名，必须指定，而且必须已经在abc中配置
	 */
	private String mappingName;
	/**
	 * 添加的实体的的编码字段名，不指定时会用”编码“来获取实体编码值
	 */
	private String codeName;
	/**
	 * 添加的实体的名称字段名，不指定时会用“姓名”来获取该名称字段值
	 */
	private String titleName;
	/**
	 * 添加的实体是否用于导入，默认为false
	 */
	private boolean forImport = false;
	/**
	 * 当forImport不设置或者为true时必须指定，用于显示在导入功能中作为导入条线的选项
	 */
	private String impTitle;
	
	public AddEntityParam(String moduleName, String mappingName) {
		this.moduleName = moduleName;
		this.mappingName = mappingName;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getMappingName() {
		return mappingName;
	}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
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
	public boolean isForImport() {
		return forImport;
	}
	public void setForImport(boolean forImport) {
		this.forImport = forImport;
	}
	public String getImpTitle() {
		return impTitle;
	}
	public void setImpTitle(String impTitle) {
		this.impTitle = impTitle;
	}
}
