package cn.sowell.datacenter.entityResolver;

import java.util.Set;

public interface FieldService {

	Set<FieldParserDescription> getFieldDescriptions(String module);
	void refreshFields();
}
