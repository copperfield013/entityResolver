package cn.sowell.datacenter.entityResolver.config;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.sowell.datacenter.entityResolver.config.abst.Composite;
import cn.sowell.datacenter.entityResolver.config.abst.Import;

class TheImport implements Import {
	private static final long serialVersionUID = 7196405654039316329L;
	private Set<Composite> composites = new LinkedHashSet<Composite>();

	@Override
	public Set<Composite> getComposites() {
		return composites;
	}

	void setComposites(Set<Composite> composites) {
		this.composites = composites;
	}
}
