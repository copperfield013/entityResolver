package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.abc.application.BizFusionContext;
import com.abc.application.FusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.panel.Integration;
import com.abc.panel.PanelFactory;

import cn.sowell.copframe.utils.Assert;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;
import cn.sowell.datacenter.entityResolver.exception.UnsupportedEntityElementException;

public abstract class AbstractFusionContextConfigResolver implements FusionContextConfigResolver{
	protected FusionContextConfig config;
	private Set<FieldParserDescription> fields;
	
	Logger logger = Logger.getLogger(AbstractFusionContextConfigResolver.class);
	
	public AbstractFusionContextConfigResolver(FusionContextConfig config) {
		super();
		Assert.notNull(config);
		this.config = config;
	}
	
	protected abstract EntityBindContext buildRootContext(Entity entity);
	
	public synchronized void setFields(Set<FieldParserDescription> dynamicFieldDescriptionSet) {
		this.fields = dynamicFieldDescriptionSet;
		
	}
	
	@Override
	public FieldParserDescription getFieldParserDescription(Long fieldId) {
		if(this.fields != null) {
			return fields.stream().filter(field->fieldId.equals(field.getFieldId())).findFirst().orElse(null);
		}
		return null;
	}
	
	
	private Entity createEntity(Map<String, Object> map, boolean ignoreUnsupportedElement) {
		Entity entity = new Entity(config.getMappingName());
		EntityBindContext rootContext = buildRootContext(entity);
		if(rootContext != null) {
			map.forEach((propName, propValue)->{
				try {
					bindElement(rootContext, propName, propValue);
				} catch (UnsupportedEntityElementException e) {
					if(!ignoreUnsupportedElement) {
						throw e;
					}
				}
			});
			((EntitiesContainedEntityProxy)rootContext.getEntity()).commit();
			return entity;
		}
		return null;
	}
	
	@Override
	public Entity createEntity(Map<String, Object> map) {
		logger.debug("==============创建Entity");
		Entity entity = createEntity(map, false);
		logger.debug(entity.toJson());
		return entity;
	}
	
	@Override
	public Entity createEntityIgnoreUnsupportedElement(Map<String, Object> map) {
		return createEntity(map, true);
	}
	
	

	private void bindElement(EntityBindContext context, String propName, Object propValue) throws UnsupportedEntityElementException {
		String[] split = propName.split("\\.", 2);
		String prefix = split[0];
		if(split.length == 1) {
			context.setValue(propName, propValue);
		}else {
			EntityBindContext elementContext = context.getElement(new PropertyNamePartitions(prefix));
			bindElement(elementContext, split[1], propValue);
		}
	}
	
	@Override
	public ModuleEntityPropertyParser createParser(Entity entity) {
		if(this.fields == null) {
			throw new RuntimeException("解析器没有初始化字段数据");
		}else {
			EntityBindContext rootContext = buildRootContext(entity);
			CommonModuleEntityPropertyParser parser = new CommonModuleEntityPropertyParser(config, rootContext, getFullKeyFieldMap());
			return parser ;
		}
	}
	
	

	private Map<String, FieldParserDescription> getFullKeyFieldMap() {
		return CollectionUtils.toMap(this.fields, field->field.getFullKey());
	}
	
	@Override
	public String saveEntity(Map<String, Object> map, Consumer<BizFusionContext> consumer) {
		BizFusionContext context = config.createContext();
		context.setSource(FusionContext.SOURCE_COMMON);
		if(consumer != null) {
			consumer.accept(context);
		}
		return saveEntity(context, map);
	}
	
	@Override
	public String saveEntity(BizFusionContext context, Map<String, Object> map) {
		Assert.notNull(context);
		Entity entity = createEntity(map);
		if(entity != null) {
			Integration integration=PanelFactory.getIntegration();
			return integration.integrate(entity, context);
		}
		throw new RuntimeException("无法根据map创建Entity");
	}
	
	@Override
	public boolean hasLoadFieldDescription() {
		return this.fields != null;
	}
	
}
