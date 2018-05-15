package cn.sowell.datacenter.entityResolver.config;

import java.util.LinkedHashSet;
import java.util.Set;

class Import {
	private Set<Composite> composites = new LinkedHashSet<Composite>();

	public Set<Composite> getComposites() {
		return composites;
	}

	public void setComposites(Set<Composite> composites) {
		this.composites = composites;
	}
}
