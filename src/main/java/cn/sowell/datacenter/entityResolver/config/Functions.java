package cn.sowell.datacenter.entityResolver.config;

import java.util.LinkedHashSet;
import java.util.Set;

class Functions {
	private Set<Function> functions = new LinkedHashSet<Function>();

	public Set<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Set<Function> functions) {
		this.functions = functions;
	}
}
