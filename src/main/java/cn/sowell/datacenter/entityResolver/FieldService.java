package cn.sowell.datacenter.entityResolver;

import java.util.Map;
import java.util.Set;

public interface FieldService {

	Set<FieldParserDescription> getFieldDescriptions(String module);
	void refreshFields();
	Map<String, Set<FieldParserDescription>> getFieldDescriptions(Set<String> moduleNames);
}
