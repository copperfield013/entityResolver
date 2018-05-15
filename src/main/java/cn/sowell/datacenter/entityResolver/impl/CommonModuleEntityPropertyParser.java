package cn.sowell.datacenter.entityResolver.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.abc.dto.ErrorInfomation;
import com.abc.mapping.entity.Entity;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.GetonlyMap;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;

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
	public Object getProperty(String propertyName, String propType) {
		Assert.hasText(propertyName);
		String[] names = TextUtils.splitToArray(propertyName, "\\.");
		
		EntityBindContext thisContext = this.context;
		for (int i = 0; i < names.length - 1; i++) {
			PropertyNamePartitions namePartitions = new PropertyNamePartitions(names[i]);
			thisContext = thisContext.getElement(namePartitions);
		}
		if(propType == null) {
			FieldParserDescription field = fieldMap.get(propertyName.replaceAll("\\[\\d+\\]", ""));
			if(field != null) {
				propType = field.getAbcType();
			}else {
				propType = "string";
			}
		}
		return thisContext.getValue(names[names.length - 1], propType);
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


}
