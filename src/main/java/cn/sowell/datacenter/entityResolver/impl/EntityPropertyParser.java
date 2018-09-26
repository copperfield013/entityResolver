package cn.sowell.datacenter.entityResolver.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.abc.mapping.entity.Entity;
import com.abc.util.ValueType;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.GetonlyMap;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;
import cn.sowell.datacenter.entityResolver.converter.FilePropertyGetter;
import cn.sowell.datacenter.entityResolver.converter.PropertyValueGetContext;
import cn.sowell.datacenter.entityResolver.converter.PropertyValueGetter;

public abstract class EntityPropertyParser extends AbstractEntityPropertyParser {

	protected FusionContextConfig config;
	
	protected EntityBindContext context;
	
	protected Object userPrinciple;
	
	protected Object propertyGetterArgument;

	
	
	EntityPropertyParser(FusionContextConfig config, EntityBindContext context, Map<String, FieldParserDescription> fieldMap, Object userPrinciple) {
		this(config, context, fieldMap, userPrinciple, null);
	}
	
	EntityPropertyParser(FusionContextConfig config, EntityBindContext context,
			Map<String, FieldParserDescription> fieldMap, Object userPrinciple, Object propertyGetterArgument) {
		super(fieldMap);
		Assert.notNull(config);
		Assert.notNull(context);
		Assert.notNull(context.getEntity());
		this.context = context;
		this.config = config;
		this.userPrinciple = userPrinciple;
		this.propertyGetterArgument = propertyGetterArgument;
	}

	@Override
	public String getCode() {
		return (String) context.getValue("唯一编码", ValueType.STRING);
	}
	
	public List<ArrayItemPropertyParser> getCompositeArray(String compositeName) {
		List<ArrayItemPropertyParser> parsers = new ArrayList<ArrayItemPropertyParser>();
		String[] names = TextUtils.splitToArray(compositeName, "\\.");
		EntityBindContext thisContext = this.context;
		for (int i = 0; i < names.length - 1; i++) {
			PropertyNamePartitions namePartitions = new PropertyNamePartitions(names[i]);
			thisContext = thisContext.getElementAutoCreate(namePartitions);
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
	
	
	
	protected Object getProperty(String relationName, String propertyName, FieldParserDescription field, ValueType propType) {
		Assert.hasText(propertyName);
		String[] names = TextUtils.splitToArray(propertyName, "\\.");
		EntityBindContext thisContext = this.context,
							parentContext = null;
		for (int i = 0; i < names.length - 1; i++) {
			PropertyNamePartitions namePartitions = new PropertyNamePartitions(names[i]);
			parentContext = thisContext;
			thisContext = thisContext.getElementAutoCreate(namePartitions);
		}
		//FieldParserDescription field = fieldMap.get(propertyName.replaceAll("\\[\\d+\\]", ""));
		String propName = names[names.length - 1];
		if(field != null) {
			if(propType == null && field != null) {
				propType = field.getAbcType();
			}
			PropertyValueGetter getter = getPropertyCGetter(field, propertyGetterArgument);
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
				c.setUserPrinciple(userPrinciple);
				c.setRelationName(relationName);
				c.setPropertyGetterArgument(propertyGetterArgument);
				
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
	static PropertyValueGetter getPropertyCGetter(FieldParserDescription field, Object propertyGetterArgument2) {
		return converters.stream().filter(converter->converter.support(field, propertyGetterArgument2)).findFirst().orElse(null);
	}


	private Map<String, List<ArrayItemPropertyParser>> arrayMap = new GetonlyMap<String, List<ArrayItemPropertyParser>>() {

		@Override
		public List<ArrayItemPropertyParser> get(Object key) {
			return getCompositeArray((String)key);
		}
	};

	public Map<String, List<ArrayItemPropertyParser>> getArrayMap() {
		return arrayMap;
	}
	

	private static class CommonPropertyGetContext implements PropertyValueGetContext{

		private EntityPropertyParser parser;
		private FieldParserDescription field;
		private String fullPropertyKey;
		private String fullPropertyPath;
		private EntityBindContext currentContext;
		private EntityBindContext rootContext;
		private EntityBindContext parentEntityContext;
		private FusionContextConfig contextConfig;
		private String currentPropertyPath;
		private Object userPrinciple;
		private String relationName;
		private Object propertyGetterArgument;

		@Override
		public EntityPropertyParser getParser() {
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

		public void setParser(EntityPropertyParser parser) {
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

		@Override
		public Object getUserPrinciple() {
			return userPrinciple;
		}

		public void setUserPrinciple(Object userPrinciple) {
			this.userPrinciple = userPrinciple;
		}

		@Override
		public String getRelationName() {
			return relationName;
		}

		public void setRelationName(String relationName) {
			this.relationName = relationName;
		}

		@Override
		public Object getPropertyGetterArgument() {
			return propertyGetterArgument;
		}


		public void setPropertyGetterArgument(Object propertyGetterArgument) {
			this.propertyGetterArgument = propertyGetterArgument;
		}


	}

}
