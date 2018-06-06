package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import cn.sowell.datacenter.entityResolver.config.abst.Config;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

class TheConfig implements Config{

	private List<Module> modules;
	
	@Override
	public List<Module> getModules() {
		return this.modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	
}
