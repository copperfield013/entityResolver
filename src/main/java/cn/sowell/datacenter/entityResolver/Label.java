package cn.sowell.datacenter.entityResolver;

import java.util.Set;

public interface Label {
	FusionContextConfig getConfig();
	String getFieldName();
	Set<String> getSubdomain();
	
}
