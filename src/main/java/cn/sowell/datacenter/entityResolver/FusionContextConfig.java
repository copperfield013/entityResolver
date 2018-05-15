package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import com.abc.application.BizFusionContext;

public interface FusionContextConfig {

	String getMappingName();

	FusionContextConfigResolver getConfigResolver();

	void loadResolver(Set<FieldParserDescription> fields);

	boolean hasLoadResolverFields();

	String getModule();

	String getCodeAttributeName();

	String getTitleAttributeName();

	BizFusionContext createContext();

	Set<Label> getAllLabels();

	Set<ImportCompositeField> getAllImportFields();

}