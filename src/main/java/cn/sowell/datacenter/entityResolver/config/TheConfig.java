package cn.sowell.datacenter.entityResolver.config;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.abst.Config;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

class TheConfig implements Config {
	private static final long serialVersionUID = -8541115350440408033L;
	private Set<Module> modules;

	@Override
	public Set<Module> getModules() {
		return modules;
	}

	void setModules(Set<Module> modules) {
		this.modules = modules;
	}
}
