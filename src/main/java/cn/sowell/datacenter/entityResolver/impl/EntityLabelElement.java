package cn.sowell.datacenter.entityResolver.impl;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.EntityElement;

public class EntityLabelElement extends EntityElement{
	Set<String> subdomain;

	public Set<String> getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(Set<String> subdomain) {
		this.subdomain = subdomain;
	}
	
}
