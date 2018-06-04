package cn.sowell.datacenter.entityResolver.config;

import cn.sowell.datacenter.entityResolver.config.abst.Function;

class TheFunction implements Function {
	private static final long serialVersionUID = -2307084352489340372L;
	private String name;

	@Override
	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}
}
