package cn.sowell.datacenter.entityResolver.config;

import cn.sowell.datacenter.entityResolver.config.abst.Composite;

class TheComposite implements Composite {
	private static final long serialVersionUID = -6017712857846140368L;
	private String name;
	private String entityId;
	private String title;
	@Override
	public String getEntityId() {
		return entityId;
	}
	void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	@Override
	public String getTitle() {
		return title;
	}
	void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
}
