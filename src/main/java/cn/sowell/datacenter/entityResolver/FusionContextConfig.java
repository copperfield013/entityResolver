package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import com.abc.hc.HCFusionContext;
import com.abc.mapping.node.ABCNode;

public interface FusionContextConfig {

	Long getMappingId();

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
	
	ABCNode getRootNode();

	boolean isStatistic();

}