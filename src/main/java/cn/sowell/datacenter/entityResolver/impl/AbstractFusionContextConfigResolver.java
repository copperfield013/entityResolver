package cn.sowell.datacenter.entityResolver.impl;

import java.util.Map;
import java.util.Map.Entry;
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
import cn.sowell.datacenter.entityResolver.impl.PropertyValueBindReport.PropertyType;

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
	
	
	private EntityComponent createEntity(Map<String, Object> map, boolean ignoreUnsupportedElement) {
		Entity entity = new Entity(config.getMappingName());
		EntityBindContext rootContext = buildRootContext(entity);
		if(rootContext != null) {
			boolean updatedFileProperty = false;
			for (Entry<String, Object> entry : map.entrySet()) {
				String propName = entry.getKey();
				Object propValue = entry.getValue();
				PropertyValueBindReport report = null;
				try {
					report = bindElement(rootContext, propName, propValue);
				} catch (UnsupportedEntityElementException e) {
					if(!ignoreUnsupportedElement) {
						throw e;
					}
				}
				if(!updatedFileProperty && report != null 
						&& report.getPropertyType() == PropertyType.FILE) {
					//修改过文件字段
					updatedFileProperty = true;
				}
			}
			((EntitiesContainedEntityProxy)rootContext.getEntity()).commit();
			boolean toCreate = entity.getStringValue(config.getCodeAttributeName()) == null;
			CommonEntityComponent cEntity = new CommonEntityComponent(entity, toCreate);
			cEntity.setSavedFile(updatedFileProperty);
			return cEntity;
		}
		return null;
	}
	
	@Override
	public EntityComponent createEntity(Map<String, Object> map) {
		logger.debug("==============创建Entity");
		EntityComponent entity = createEntity(map, false);
		logger.debug(entity.toJson());
		return entity;
	}
	
	@Override
	public EntityComponent createEntityIgnoreUnsupportedElement(Map<String, Object> map) {
		return createEntity(map, true);
	}
	
	

	private PropertyValueBindReport bindElement(EntityBindContext context, String propName, Object propValue) throws UnsupportedEntityElementException {
		String[] split = propName.split("\\.", 2);
		String prefix = split[0];
		if(split.length == 1) {
			return context.setValue(propName, propValue);
		}else {
			EntityBindContext elementContext = context.getElement(new PropertyNamePartitions(prefix));
			return bindElement(elementContext, split[1], propValue);
		}
	}
	
	@Override
	public ModuleEntityPropertyParser createParser(Entity entity, Object user) {
		if(this.fields == null) {
			throw new RuntimeException("解析器没有初始化字段数据");
		}else {
			EntityBindContext rootContext = buildRootContext(entity);
			CommonModuleEntityPropertyParser parser = new CommonModuleEntityPropertyParser(config, rootContext, getFullKeyFieldMap(), user);
			return parser ;
		}
	}
	
	
	@Override
	public RelationEntityPropertyParser createRelationParser(Entity entity, String relationName, Object user) {
		if(this.fields == null) {
			throw new RuntimeException("解析器没有初始化字段数据");
		}else {
			return new RelationEntityPropertyParser(config, relationName, getFullKeyFieldMap(), user, entity);
		}
	}
	

	private Map<String, FieldParserDescription> getFullKeyFieldMap() {
		return CollectionUtils.toMap(this.fields, field->field.getFullKey());
	}
	
	@Override
	public String saveEntity(Map<String, Object> map, Consumer<BizFusionContext> consumer, Object user) {
		BizFusionContext context = config.getCurrentContext(user);
		context.setSource(FusionContext.SOURCE_COMMON);
		if(consumer != null) {
			consumer.accept(context);
		}
		return saveEntity(context, map);
	}
	
	@Override
	public String saveEntity(BizFusionContext context, Map<String, Object> map) {
		Assert.notNull(context);
		EntityComponent entity = createEntity(map);
		if(entity != null) {
			Integration integration=PanelFactory.getIntegration();
			String code = integration.integrate(entity.getEntity(), context);
			return code;
		}
		throw new RuntimeException("无法根据map创建Entity");
	}
	
	@Override
	public boolean hasLoadFieldDescription() {
		return this.fields != null;
	}
	
}
