package cn.sowell.datacenter.entityResolver.config;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Functions;
import cn.sowell.datacenter.entityResolver.config.abst.Import;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

class TheModule implements Module {
	private static final long serialVersionUID = 1177752285709607977L;
	private String name;
	private String title;
	private boolean disabled;
	private Set<Entity> entities;
	private Import imp;
	private Functions functions;
	@Override
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	@Override
	public String getTitle() {
		return title;
	}
	void setTitle(String title) {
		this.title = title;
	}
	@Override
	public Set<Entity> getEntities() {
		return entities;
	}
	void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}
	@Override
	public Entity getDefautEntity() {
		if(this.entities != null) {
			for (Entity entity : entities) {
				if(entity.isDefault()) {
					return entity;
				}
			}
		}
		return null;
	}
	@Override
	public Import getImport() {
		return imp;
	}
	void setImport(Import imp) {
		this.imp = imp;
	}
	@Override
	public Functions getFunctions() {
		return this.functions;
	}
	void setFunctions(Functions functions) {
		this.functions = functions;
	}
	public boolean isDisabled() {
		return disabled;
	}
	void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
