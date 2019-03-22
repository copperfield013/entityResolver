package cn.sowell.datacenter.entityResolver.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.abc.application.BizFusionContext;
import com.abc.application.FusionContext;
import com.abc.dto.ErrorInfomation;
import com.abc.mapping.entity.Entity;
import com.abc.panel.Integration;
import com.abc.panel.IntegrationMsg;
import com.abc.panel.PanelFactory;
import com.abc.rrc.query.queryrecord.criteria.QueryParameter;

import cn.sowell.copframe.utils.Assert;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
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
	
	public static final String PROP_FLAG = ".$$flag$$";
	private EntityComponent createEntity(Map<String, Object> map, boolean ignoreUnsupportedElement) {
		Entity entity = new Entity(config.getMappingName());
		EntityBindContext rootContext = buildRootContext(entity);
		if(rootContext != null) {
			boolean updatedFileProperty = false;
			Set<String> flagCompositeName = new HashSet<>();
			for (Entry<String, Object> entry : map.entrySet()) {
				String propName = entry.getKey();
				Object propValue = entry.getValue();
				if(propName.endsWith(PROP_FLAG)) {
					if("true".equals(propValue)) {
						flagCompositeName.add(propName.substring(0, propName.length() - PROP_FLAG.length()));
					}
				}else {
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
			}
			checkAndRemoveComposite(rootContext, flagCompositeName);
			((EntitiesContainedEntityProxy)rootContext.getEntity()).commit();
			boolean toCreate = entity.getStringValue(config.getCodeAttributeName()) == null;
			boolean hasTitle = TextUtils.hasText(entity.getStringValue(config.getTitleAttributeName()));
			CommonEntityComponent cEntity = new CommonEntityComponent(entity, toCreate, hasTitle);
			cEntity.setSavedFile(updatedFileProperty);
			return cEntity;
		}
		return null;
	}
	
	private void checkAndRemoveComposite(EntityBindContext rootContext, Set<String> flagCompositeNames) {
		if(rootContext != null && flagCompositeNames != null) {
			for (String compositeName : flagCompositeNames) {
				String[] split = compositeName.split("\\.");
				boolean toRemove = false;
				EntityBindContext context = rootContext;
				for (int i = 0; i < split.length; i++) {
					context = context.getElementIfExists(new PropertyNamePartitions(split[i]));
					if(context == null) {
						toRemove = true;
						break;
					}
				}
				if(toRemove) {
					context = rootContext;
					for (int i = 0; i < split.length; i++) {
						if(i == split.length - 1) {
							context.removeAllComposite(split[i]);
						}else {
							context = context.getElementAutoCreate(new PropertyNamePartitions(split[i]));
						}
					}
				}
			}
		}
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
			EntityBindContext elementContext = context.getElementAutoCreate(new PropertyNamePartitions(prefix));
			return bindElement(elementContext, split[1], propValue);
		}
	}
	
	@Override
	public ModuleEntityPropertyParser createParser(Entity entity, Object user, Object propertyGetterArgument) {
		if(this.fields == null) {
			throw new RuntimeException("解析器没有初始化字段数据");
		}else {
			EntityBindContext rootContext = buildRootContext(entity);
			CommonModuleEntityPropertyParser parser = new CommonModuleEntityPropertyParser(config, rootContext, getFullKeyFieldMap(), user, propertyGetterArgument);
			//获得错误信息
			if(parser.getCode() != null) {
				parser.setErrors(new ArrayList<ErrorInfomation>());
				/*BizFusionContext context = config.getCurrentContext(user);
				Discoverer discoverer = PanelFactory.getDiscoverer(context);
				List<ErrorInfomation> errors = discoverer.trackErrorInfos(parser.getCode());
				parser.setErrors(errors);*/
			}
			return parser ;
		}
	}
	
	
	@Override
	public RelSelectionEntityPropertyParser createRelationParser(Entity entity, String relationName, Object user) {
		if(this.fields == null) {
			throw new RuntimeException("解析器没有初始化字段数据");
		}else {
			return new RelSelectionEntityPropertyParser(config, relationName, getFullKeyFieldMap(), user, entity);
		}
	}
	
	@Override
	public RabcModuleEntityPropertyParser createRabcEntityParser(Entity entity, Object user,
			Object propertyGetterArgument) {
		if(this.fields == null) {
			throw new RuntimeException("解析器没有初始化字段数据");
		}else {
			EntityBindContext rootContext = buildRootContext(entity);
			RabcModuleEntityPropertyParser parser = new RabcModuleEntityPropertyParser(config, rootContext, getFullKeyFieldMap(), user, propertyGetterArgument);
			//获得错误信息
			if(parser.getCode() != null) {
				parser.setErrors(new ArrayList<ErrorInfomation>());
				/*BizFusionContext context = config.getCurrentContext(user);
				Discoverer discoverer = PanelFactory.getDiscoverer(context);
				List<ErrorInfomation> errors = discoverer.trackErrorInfos(parser.getCode());
				parser.setErrors(errors);*/
			}
			return parser ;
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
		return saveEntity(context, map, null);
	}
	
	@Override
	public String saveEntity(Map<String, Object> entityMap, Consumer<BizFusionContext> consumer, Object user,
			Map<String, QueryParameter> criteriasMap) {
		BizFusionContext context = config.getCurrentContext(user);
		context.setSource(FusionContext.SOURCE_COMMON);
		if(consumer != null) {
			consumer.accept(context);
		}
		return saveEntity(context, entityMap, criteriasMap);
	}
	
	@Override
	public String saveEntity(BizFusionContext context, Map<String, Object> map, Map<String, QueryParameter> criteriasMap) {
		Assert.notNull(context);
		EntityComponent entity = createEntity(map);
		if(criteriasMap != null) {
			criteriasMap.forEach((key, qp)->{
				entity.getEntity().putQueryParameter(key, qp);
			});
		}
		if(entity != null) {
			Integration integration=PanelFactory.getIntegration();
			IntegrationMsg msg = integration.integrate(context, entity.getEntity());
			if(msg.success()) {
				logger.info("integrate实体成功");
				logger.info(msg.toString());
				return msg.getCode();
			}else {
				logger.error("integrate实体失败");
				logger.info(msg.toString());
			}
		}
		throw new RuntimeException("无法根据map创建Entity");
	}
	
	@Override
	public boolean hasLoadFieldDescription() {
		return this.fields != null;
	}
	
}
