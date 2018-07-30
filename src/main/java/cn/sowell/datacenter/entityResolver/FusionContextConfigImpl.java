package cn.sowell.datacenter.entityResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import com.abc.application.BizFusionContext;

import cn.sowell.datacenter.entityResolver.impl.ABCNodeFusionContextConfigResolver;

public class FusionContextConfigImpl implements FusionContextConfig{
	private String mappingName;
	private String module;
	private String codeAttributeName = "唯一编码";
	private String titleAttributeName = "姓名";
	private FusionContextConfigResolver configResolver;
	private boolean loadResolverFieldsFlag = false;
	private UserCodeService userCodeService;
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getMappingName()
	 */
	@Override
	public String getMappingName() {
		return mappingName;
	}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getConfigResolver()
	 */
	@Override
	public FusionContextConfigResolver getConfigResolver() {
		return configResolver;
	}
	public void setConfigResolver(FusionContextConfigResolver configResolver) {
		this.configResolver = configResolver;
		loadResolverFieldsFlag = configResolver != null && configResolver.hasLoadFieldDescription();
	}
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#loadResolver(java.util.Set)
	 */
	@Override
	public void loadResolver(Set<FieldParserDescription> fields) {
		ABCNodeFusionContextConfigResolver resolver = new ABCNodeFusionContextConfigResolver(this);
		if(fields != null) {
			resolver.setFields(fields);
		}
		setConfigResolver(resolver);
	}
	/**
	 * 
	 */
	@Override
	public boolean hasLoadResolverFields() {
		return loadResolverFieldsFlag;
	}
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getModule()
	 */
	@Override
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getCodeAttributeName()
	 */
	@Override
	public String getCodeAttributeName() {
		return codeAttributeName;
	}
	public void setCodeAttributeName(String codeAttributeName) {
		this.codeAttributeName = codeAttributeName;
	}
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getTitleAttributeName()
	 */
	@Override
	public String getTitleAttributeName() {
		return titleAttributeName;
	}
	public void setTitleAttributeName(String titleAttributeName) {
		this.titleAttributeName = titleAttributeName;
	}
	
	
	private ThreadLocal<Map<Object, BizFusionContext>> userFusionContextLocal = new ThreadLocal<>();
	
	@Override
	public BizFusionContext createNewContext(Object userPrinciple) {
		BizFusionContext context = new BizFusionContext();
		context.setMappingName(getMappingName());
		context.setUserCode(userCodeService.getUserCode(userPrinciple));
		return context;
	}
	
	@Override
	public BizFusionContext getCurrentContext(Object user) {
		Assert.notNull(user);
		Map<Object, BizFusionContext> map = null;
		synchronized (userFusionContextLocal) {
			map = userFusionContextLocal.get();
			if(userFusionContextLocal.get() == null) {
				map = new HashMap<>();
				map.put(user, createNewContext(user));
				userFusionContextLocal.set(map);
			}
		}
		synchronized (map) {
			if(!map.containsKey(user)) {
				map.put(user, createNewContext(user));
			}
			return map.get(user);
		}
	}
	
	@Override
	public Set<Label> getAllLabels() {
		FusionContextConfigResolver cr = getConfigResolver();
		if(cr instanceof ABCNodeFusionContextConfigResolver) {
			return ((ABCNodeFusionContextConfigResolver)cr).getAllLabels();
		}
		return null;
	}
	
	@Override
	public Set<ImportCompositeField> getAllImportFields() {
		FusionContextConfigResolver cr = getConfigResolver();
		if(cr instanceof ABCNodeFusionContextConfigResolver) {
			return ((ABCNodeFusionContextConfigResolver)cr).getAllImportFields();
		}
		return null;
	}
	public void setUserCodeService(UserCodeService userCodeService) {
		this.userCodeService = userCodeService;
	}
}
