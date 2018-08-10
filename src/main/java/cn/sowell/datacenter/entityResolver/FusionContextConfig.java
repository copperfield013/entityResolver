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

	
	BizFusionContext getCurrentContext(Object user);
	
	/**
	 * 
	 * @param userPrinciple
	 * @return
	 * @see {@link UserCodeService#getUserCode(Object)}
	 */
	BizFusionContext createNewContext(Object userPrinciple);

	Set<Label> getAllLabels();

	Set<ImportCompositeField> getAllImportFields();

	void removeEntity(String code, Object userPrinciple);

	BizFusionContext createRelationContext(String relationName, Object userPrinciple);


}