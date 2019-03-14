package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import com.abc.application.BizFusionContext;
import com.abc.mapping.node.ABCNode;

public interface FusionContextConfig {

	Long getMappingId();

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

	void removeEntity(String code, Object userPrinciple);

	BizFusionContext createRelationContext(String relationName, Object userPrinciple);

	String getMappingName();
	
	ABCNode getRootNode();

	boolean isStatistic();

}