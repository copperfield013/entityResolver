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

	/**
	 * 获得当前线程的BizFusionContext，在当前线程下，返回的对象都是同一个对象
	 * @return
	 */
	BizFusionContext getCurrentContext();
	
	/**
	 * 创建并返回一个新的BizFusionContext对象
	 * @return
	 */
	BizFusionContext createNewContext();

	Set<Label> getAllLabels();

	Set<ImportCompositeField> getAllImportFields();

}