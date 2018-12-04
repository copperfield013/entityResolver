package cn.sowell.datacenter.entityResolver.config.param;

import java.io.Serializable;

import org.springframework.util.Assert;

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
	 * 模块对应配置名，必须指定，而且必须已经在abc中配置
	 */
	private Long mappingId;
	
	/**
	 * 模块对应配置的编码字段名，不指定时会用”编码“来获取实体编码值
	 */
	private String codeName;
	
	/**
	 * 模块对应配置的名称字段名，不指定时会用“姓名”来获取该名称字段值
	 */
	private String titleName;
	
	public CreateModuleParam(String moduleTitle, Long mappingId) {
		Assert.hasText(moduleTitle);
		Assert.notNull(mappingId);
		this.moduleTitle = moduleTitle;
		this.mappingId = mappingId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleTitle() {
		return moduleTitle;
	}

	public void setModuleTitle(String moduleTitle) {
		this.moduleTitle = moduleTitle;
	}

	public Long getMappingId() {
		return this.mappingId;
	}

	public void setMappingId(Long mappingId) {
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
	
}
