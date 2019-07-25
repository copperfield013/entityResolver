package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import cho.carbon.hc.HCFusionContext;
import cho.carbon.meta.struc.er.Struc;

public interface FusionContextConfig {

	Integer getMappingId();

	FusionContextConfigResolver getConfigResolver();

	void loadResolver(Set<FieldParserDescription> fields);

	boolean hasLoadResolverFields();

	String getModule();

	String getCodeAttributeName();

	String getTitleAttributeName();

	
	HCFusionContext getCurrentContext(Object user);
	
	/**
	 * 
	 * @param userPrinciple
	 * @return
	 * @see {@link UserCodeService#getUserCode(Object)}
	 */
	HCFusionContext createNewContext(Object userPrinciple);

	Set<Label> getAllLabels();

	void removeEntity(String code, Object userPrinciple);

	HCFusionContext createRelationContext(String relationName, Object userPrinciple);

	String getMappingName();
	
	Struc getRootNode();

	boolean isStatistic();

}