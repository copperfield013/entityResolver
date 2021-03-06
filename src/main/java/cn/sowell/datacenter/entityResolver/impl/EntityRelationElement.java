package cn.sowell.datacenter.entityResolver.impl;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.EntityElement;

public class EntityRelationElement extends EntityElement {
	private String entityName;
	private String fullAbcattrName;
	private Set<String> subdomain;
	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Set<String> getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(Set<String> subdomain) {
		this.subdomain = subdomain;
	}

	public String getFullTitle() {
		return fullAbcattrName;
	}

	public void setFullTitle(String fullAbcattrName) {
		this.fullAbcattrName = fullAbcattrName;
	}

	
}
