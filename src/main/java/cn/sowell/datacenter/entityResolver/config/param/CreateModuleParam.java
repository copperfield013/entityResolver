package cn.sowell.datacenter.entityResolver.config.param;

import java.io.Serializable;

public class CreateModuleParam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3901899000144778042L;

	/**
	 * 模块名，需要全局唯一，不指定时会自动生成10位的随机码
	 */
	private String moduleName;
	
	/**
	 * 模块标题，必须指定
	 */
	private String moduleTitle;
	
	/**
	 * 默认模块的实体的id，需要全局唯一，不指定时会自动生成10位随机码
	 */
	private String defEntityId;
	
	/**
	 * 模块默认的实体的对应配置名，必须指定，而且必须已经在abc中配置
	 */
	private String defMappingName;
	
	/**
	 * 模块默认实体的的编码字段名，不指定时会用”编码“来获取实体编码值
	 */
	private String defCodeName;
	
	/**
	 * 模块默认实体的名称字段名，不指定时会用“姓名”来获取该名称字段值
	 */
	private String defTitleName;
	
	/**
	 * 模块默认实体是否用来导入，默认为false
	 */
	private boolean defForImport = false;
	
	/**
	 * 用于显示在导入功能中作为导入条线的选项
	 */
	private String impTitle;
	
	public CreateModuleParam(String moduleTitle, String defMappingName) {
		this.moduleTitle = moduleTitle;
		this.defMappingName = defMappingName;
	}
	
	public CreateModuleParam(String moduleTitle, String defMappingName, String impTitle) {
		this.moduleTitle = moduleTitle;
		this.defMappingName = defMappingName;
		this.impTitle = impTitle;
	}
	/**
	 * @see #moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}
	/**
	 * @see #moduleName
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleTitle() {
		return moduleTitle;
	}
	public void setModuleTitle(String moduleTitle) {
		this.moduleTitle = moduleTitle;
	}
	public String getDefEntityId() {
		return defEntityId;
	}
	public void setDefEntityId(String defEntityId) {
		this.defEntityId = defEntityId;
	}
	public String getDefMappingName() {
		return defMappingName;
	}
	public void setDefMappingName(String defMappingName) {
		this.defMappingName = defMappingName;
	}
	public String getDefCodeName() {
		return defCodeName;
	}
	public void setDefCodeName(String defCodeName) {
		this.defCodeName = defCodeName;
	}
	public String getDefTitleName() {
		return defTitleName;
	}
	public void setDefTitleName(String defTitleName) {
		this.defTitleName = defTitleName;
	}
	public boolean isDefForImport() {
		return defForImport;
	}
	public void setDefForImport(boolean defForImport) {
		this.defForImport = defForImport;
	}
	public String getImpTitle() {
		return impTitle;
	}
	public void setImpTitle(String impTitle) {
		this.impTitle = impTitle;
	}
	
}
