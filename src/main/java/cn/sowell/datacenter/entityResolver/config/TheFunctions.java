package cn.sowell.datacenter.entityResolver.config;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.abst.Function;
import cn.sowell.datacenter.entityResolver.config.abst.Functions;

class TheFunctions implements Functions {
	private static final long serialVersionUID = 1497336718811842755L;
	private Set<Function> functions = new LinkedHashSet<Function>();

	@Override
	public Set<Function> getFunctions() {
		return functions;
	}

	void setFunctions(Set<Function> functions) {
		this.functions = functions;
	}
}
