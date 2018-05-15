package cn.sowell.datacenter.entityResolver.config;

import java.util.Set;

class Module {
	private String name;
	private String title;
	private Set<Entity> entities;
	private Import imp;
	private Functions functions;
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
	public Set<Entity> getEntities() {
		return entities;
	}
	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}
	public Import getImport() {
		return imp;
	}
	public void setImport(Import imp) {
		this.imp = imp;
	}
	public Functions getFunctions() {
		return this.functions;
	}
	public void setFunctions(Functions functions) {
		this.functions = functions;
	}
}
