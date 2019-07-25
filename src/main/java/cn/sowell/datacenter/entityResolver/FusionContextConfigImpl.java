package cn.sowell.datacenter.entityResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import cho.carbon.hc.HCFusionContext;
import cho.carbon.hc.HCRemovedFusionContext;
import cho.carbon.hc.RemovedFusionContext;
import cho.carbon.meta.struc.er.Struc;
import cho.carbon.meta.struc.er.StrucContainer;
import cho.carbon.panel.IntegrationMsg;
import cho.carbon.panel.PanelFactory;
import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeFusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;

public class FusionContextConfigImpl implements FusionContextConfig{
	private final Integer mappingId;
	private String module;
	private String codeAttributeName = ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL;
	private String titleAttributeName = "姓名";
	private FusionContextConfigResolver configResolver;
	private boolean loadResolverFieldsFlag = false;
	private UserCodeService userCodeService;
	private Struc rootNode;
	
	static Logger logger = Logger.getLogger(FusionContextConfigImpl.class);
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getMappingName()
	 */
	
	public FusionContextConfigImpl(Integer mappingId) {
		this.mappingId = mappingId;
		try {
			logger.debug("加载配置[mappingId=" + mappingId + "]");
			this.rootNode = StrucContainer.findStruc(mappingId);
			if(this.rootNode == null) {
				logger.error("配置[mappingId=" + mappingId + "]不存在或解析错误");
				throw new UnconfiuredFusionException("配置[mappingId=" + mappingId + "]不存在或解析错误");
			}
		} catch (Exception e) {
			throw new RuntimeException("初始化ABC配置时发生错误[mappingId=" + mappingId + "]", e);
		}
			
	}
	
	@Override
	public Integer getMappingId() {
		return mappingId;
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
	
	
	private ThreadLocal<Map<Object, HCFusionContext>> userFusionContextLocal = new ThreadLocal<>();
	
	@Override
	public HCFusionContext createNewContext(Object userPrinciple) {
		HCFusionContext context = new HCFusionContext();
		context.setStrucTitle(getMappingName());
		context.setUserCode(userCodeService.getUserCode(userPrinciple));
		return context;
	}
	
	@Override
	public HCFusionContext getCurrentContext(Object user) {
		Assert.notNull(user);
		Map<Object, HCFusionContext> map = null;
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
	public HCFusionContext createRelationContext(String relationName, Object userPrinciple) {
		Assert.hasText(relationName);
		HCFusionContext context = new HCFusionContext();
		context.setStrucTitle(getMappingName() + "." + relationName);
		context.setUserCode(userCodeService.getUserCode(userPrinciple));
		return context;
	}
	
	@Override
	public Set<Label> getAllLabels() {
		FusionContextConfigResolver cr = getConfigResolver();
		if(cr instanceof ABCNodeFusionContextConfigResolver) {
			return ((ABCNodeFusionContextConfigResolver)cr).getAllLabels();
		}
		return null;
	}
	
	public void setUserCodeService(UserCodeService userCodeService) {
		this.userCodeService = userCodeService;
	}
	
	@Override
	public void removeEntity(String code, Object userPrinciple) {
		Assert.hasText(code);
		RemovedFusionContext appInfo=new HCRemovedFusionContext(getMappingName(), code, userCodeService.getUserCode(userPrinciple), "list-delete" );
		IntegrationMsg msg = PanelFactory.getIntegration().remove(appInfo);
		if(!msg.success()){
			throw new RuntimeException("删除失败");
		}
	}
	
	@Override
	public String getMappingName() {
		return rootNode.getTitle();
	}
	
	
	@Override
	public Struc getRootNode() {
		return rootNode;
	}
	
	@Override
	public boolean isStatistic() {
		return PanelFactory.getStatGenerator().isStatEntity(rootNode.getItemCode());
	}
}
