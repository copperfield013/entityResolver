package cn.sowell.datacenter.entityResolver;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.abc.application.BizFusionContext;
import com.abc.application.RemovedFusionContext;
import com.abc.mapping.conf.MappingContainer;
import com.abc.mapping.exception.ABCNodeLoadException;
import com.abc.mapping.node.ABCNode;
import com.abc.panel.IntegrationMsg;
import com.abc.panel.PanelFactory;

import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeFusionContextConfigResolver;

public class FusionContextConfigImpl implements FusionContextConfig{
	private final Long mappingId;
	private String module;
	private String codeAttributeName = "唯一编码";
	private String titleAttributeName = "姓名";
	private FusionContextConfigResolver configResolver;
	private boolean loadResolverFieldsFlag = false;
	private UserCodeService userCodeService;
	private ABCNode rootNode;
	
	static Logger logger = Logger.getLogger(FusionContextConfigImpl.class);
	/* (non-Javadoc)
	 * @see cn.sowell.datacenter.entityResolver.FusionContextConfig#getMappingName()
	 */
	
	public FusionContextConfigImpl(Long mappingId) {
		this.mappingId = mappingId;
		try {
			logger.debug("加载配置[mappingId=" + mappingId + "]");
			this.rootNode = MappingContainer.getABCNode(BigInteger.valueOf(mappingId));
		} catch (ABCNodeLoadException e) {
			logger.error("配置[mappingId=" + mappingId + "]不存在或解析错误");
			throw new UnconfiuredFusionException("配置[mappingId=" + mappingId + "]不存在或解析错误");
		} catch (Exception e) {
			throw new RuntimeException("初始化ABC配置时发生错误[mappingId=" + mappingId + "]", e);
		}
	}
	
	@Override
	public Long getMappingId() {
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
	public BizFusionContext createRelationContext(String relationName, Object userPrinciple) {
		Assert.hasText(relationName);
		BizFusionContext context = new BizFusionContext();
		context.setMappingName(getMappingName() + "." + relationName);
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
	
	@Override
	public void removeEntity(String code, Object userPrinciple) {
		Assert.hasText(code);
		RemovedFusionContext appInfo=new RemovedFusionContext(code, userCodeService.getUserCode(userPrinciple), "list-delete" );
		appInfo.setMappingName(getMappingName());
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
	public ABCNode getRootNode() {
		return rootNode;
	}
	
	
}
