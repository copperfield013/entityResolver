package cn.sowell.datacenter.entityResolver.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.abc.dto.ErrorInfomation;
import com.abc.mapping.entity.Entity;
import com.abc.util.ValueType;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.GetonlyMap;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;
import cn.sowell.datacenter.entityResolver.converter.FilePropertyGetter;
import cn.sowell.datacenter.entityResolver.converter.PropertyValueGetContext;
import cn.sowell.datacenter.entityResolver.converter.PropertyValueGetter;

class CommonModuleEntityPropertyParser extends AbstractEntityPropertyParser implements ModuleEntityPropertyParser{

	private FusionContextConfig config;
	
	private EntityBindContext context;

	private List<ErrorInfomation> errors;
	
	
	
	
	CommonModuleEntityPropertyParser(FusionContextConfig config, EntityBindContext context, Map<String, FieldParserDescription> fieldMap) {
		super(fieldMap);
		Assert.notNull(config);
		Assert.notNull(context);
		Assert.notNull(context.getEntity());
		this.context = context;
		this.config = config;
	}
	
	@Override
	public String getCode() {
		return this.getId();
	}
	
	@Override
	public String getId() {
		return (String) getProperty(config.getCodeAttributeName());
	}
	
	@Override
	public String getTitle() {
		return (String) getProperty(config.getTitleAttributeName());
	}
	
	@Override
	public List<ArrayItemPropertyParser> getCompositeArray(String compositeName) {
		List<ArrayItemPropertyParser> parsers = new ArrayList<ArrayItemPropertyParser>();
		String[] names = TextUtils.splitToArray(compositeName, "\\.");
		EntityBindContext thisContext = this.context;
		for (int i = 0; i < names.length - 1; i++) {
			PropertyNamePartitions namePartitions = new PropertyNamePartitions(names[i]);
			thisContext = thisContext.getElement(namePartitions);
		}
		try {
			String name = names[names.length - 1];
			Entity entity = (Entity) thisContext.getEntity().getEntity();
			@SuppressWarnings("rawtypes")
			List compositeEntities = null;
			try {
				compositeEntities = entity.getMultiAttrEntity(name);
			} catch (Exception e) {
			}
			if(compositeEntities == null) {
				compositeEntities = entity.getRelations(name);
			}
			if(compositeEntities != null) {
				for (int i = 0; i < compositeEntities.size(); i++) {
					ArrayItemPropertyParser parser = new ArrayItemPropertyParser(this, name, i, fieldMap);
					parsers.add(parser);
				}
			}
		} catch (Exception e) {
		}
		return parsers;
	}
	
	@Override
	public Object getProperty(String propertyName, ValueType propType) {
		Assert.hasText(propertyName);
		String[] names = TextUtils.splitToArray(propertyName, "\\.");
		
		EntityBindContext thisContext = this.context,
							parentContext = null;
		for (int i = 0; i < names.length - 1; i++) {
			PropertyNamePartitions namePartitions = new PropertyNamePartitions(names[i]);
			parentContext = thisContext;
			thisContext = thisContext.getElement(namePartitions);
		}
		FieldParserDescription field = fieldMap.get(propertyName.replaceAll("\\[\\d+\\]", ""));
		String propName = names[names.length - 1];
		if(field != null) {
			if(propType == null && field != null) {
				propType = field.getAbcType();
			}
			PropertyValueGetter getter = getPropertyCGetter(field);
			if(getter != null) {
				CommonPropertyGetContext c = new CommonPropertyGetContext();
				c.setParser(this);
				c.setRootContext(this.context);
				c.setContextConfig(this.config);
				c.setCurrentContext(thisContext);
				c.setCurrentPropertyPath(propName);
				c.setField(field);
				c.setFullPropertyKey(field.getFullKey());
				c.setFullPropertyPath(propertyName);
				c.setParentEntityContext(parentContext);
				return getter.invoke(c);
			}
		}
		if(propType == null) {
			propType = ValueType.STRING;
		}
		return thisContext.getValue(propName, propType);
	}

	
	@SuppressWarnings("serial")
	final static List<PropertyValueGetter> converters = new ArrayList<PropertyValueGetter>() {
		{
			add(new FilePropertyGetter());
		}
	};
	static PropertyValueGetter getPropertyCGetter(FieldParserDescription field) {
		return converters.stream().filter(converter->converter.support(field)).findFirst().orElse(null);
	}

	@Override
	public List<ErrorInfomation> getErrors() {
		return errors;
	}
	
	@Override
	public void setErrors(List<ErrorInfomation> errors) {
		this.errors = errors;
	}

	
	private Map<String, List<ArrayItemPropertyParser>> arrayMap = new GetonlyMap<String, List<ArrayItemPropertyParser>>() {

		@Override
		public List<ArrayItemPropertyParser> get(Object key) {
			return getCompositeArray((String)key);
		}
	};


	@Override
	public Map<String, List<ArrayItemPropertyParser>> getArrayMap() {
		return arrayMap;
	}
	
	
	private static class CommonPropertyGetContext implements PropertyValueGetContext{

		private ModuleEntityPropertyParser parser;
		private FieldParserDescription field;
		private String fullPropertyKey;
		private String fullPropertyPath;
		private EntityBindContext currentContext;
		private EntityBindContext rootContext;
		private EntityBindContext parentEntityContext;
		private FusionContextConfig contextConfig;
		private String currentPropertyPath;

		@Override
		public ModuleEntityPropertyParser getParser() {
			return this.parser;
		}
		
		@Override
		public EntityBindContext getRootContext() {
			return this.rootContext;
		}
		
		public void setRootContext(EntityBindContext context) {
			this.rootContext = context;
		}

		@Override
		public FieldParserDescription getField() {
			return this.field;
		}

		@Override
		public String getFullPropertyKey() {
			return this.fullPropertyKey;
		}

		@Override
		public String getFullPropertyPath() {
			return this.fullPropertyPath;
		}

		@Override
		public EntityBindContext getCurrentContext() {
			return this.currentContext;
		}

		@Override
		public String getCurrentPropertyPath() {
			return this.currentPropertyPath;
		}

		@Override
		public FusionContextConfig getContextConfig() {
			return this.contextConfig;
		}

		public void setParser(ModuleEntityPropertyParser parser) {
			this.parser = parser;
		}

		public void setField(FieldParserDescription field) {
			this.field = field;
		}

		public void setFullPropertyKey(String fullPropertyKey) {
			this.fullPropertyKey = fullPropertyKey;
		}

		public void setFullPropertyPath(String fullPropertyPath) {
			this.fullPropertyPath = fullPropertyPath;
		}

		public void setCurrentContext(EntityBindContext currentContext) {
			this.currentContext = currentContext;
		}

		public void setContextConfig(FusionContextConfig contextConfig) {
			this.contextConfig = contextConfig;
		}

		public void setCurrentPropertyPath(String currentPropertyPath) {
			this.currentPropertyPath = currentPropertyPath;
		}
		@Override
		public EntityBindContext getParentEntityContext() {
			return parentEntityContext;
		}

		public void setParentEntityContext(EntityBindContext parentEntityContext) {
			this.parentEntityContext = parentEntityContext;
		}

		
	}

}
