package cn.sowell.datacenter.entityResolver.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.SimpleEntity;
import com.abc.util.ValueType;

import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.EntityProxy;
import cn.sowell.datacenter.entityResolver.PropertyTranslator;
import cn.sowell.datacenter.entityResolver.translator.StringDateTranslator;
import cn.sowell.datacenter.entityResolver.translator.StringFloatTranslator;
import cn.sowell.datacenter.entityResolver.translator.StringIntTranslator;
import cn.sowell.datacenter.entityResolver.valsetter.FileValueSetter;
import cn.sowell.datacenter.entityResolver.valsetter.PropertyValueSetter;

public abstract class AbstractEntityBindContext implements EntityBindContext {
	protected EntityProxy entity;
	
	Logger logger = Logger.getLogger(AbstractEntityBindContext.class);
	
	
	public AbstractEntityBindContext(EntityProxy entity) {
		super();
		this.entity = entity;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object transfer(Object propValue, ValueType dataType) {
		PropertyTranslator translator = getTranslator(propValue, dataType);
		if(translator != null) {
			return translator.transfer(propValue);
		}else {
			return propValue;
		}
	}
	
	@Override
	public EntityProxy getEntity() {
		return entity;
	}
	@SuppressWarnings("rawtypes")
	static Map<PropertyTranslator, ValueType> tranlastorMap = new HashMap<>();
	static Set<PropertyValueSetter> pvSetters = new HashSet<>();
	static {
		tranlastorMap.put(new StringDateTranslator(), ValueType.DATE);
		tranlastorMap.put(new StringIntTranslator(), ValueType.INT);
		tranlastorMap.put(new StringFloatTranslator(), ValueType.FLOAT);
		
		pvSetters.add(new FileValueSetter());
		
	}
	
	static PropertyValueSetter getValueSetter(String propName, Object val, EntityAttrElement eElement) {
		return pvSetters.stream()
				.filter(setter->setter.support(eElement.getDataType(), val))
				.findFirst().orElse(null);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static PropertyTranslator getTranslator(Object propValue, ValueType dataType) {
		for (Entry<PropertyTranslator, ValueType> entry : tranlastorMap.entrySet()) {
			if(entry.getValue().equals(dataType)
				&& entry.getKey().check(propValue)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	@Override
	public Object getValue(String propName, ValueType abctype) {
		EntityProxy proxy = getEntity();
		return proxy.getTypeValue(propName, abctype);
	}
	
	@Override
	public PropertyValueBindReport setValue(String propName, Object propValue) {
		CommonPropertyValueBindReport report = new CommonPropertyValueBindReport();
		if(entity.preprocessValue(propName, propValue)) {
			report.setValuePreprocessed(true);
			EntityElement eElement = getEntityElement(propName);
			report.setPropertyEntityElement(eElement);
			if(filterEntityElement(eElement, propValue)) {
				report.setEntittyElementFiltered(true);
				if(eElement instanceof EntityAttrElement) {
					Object val = transfer(propValue, ((EntityAttrElement) eElement).getDataType());	
					report.setValueAsNull(val == null);
					PropertyValueSetter setter = getValueSetter(propName, val, (EntityAttrElement) eElement);
					if(setter != null) {
						report.setPropertyValueSetter(setter);
						setter.invoke(entity, propName, val, report);
					}else {
						entity.putValue(propName, val);
					}
				}else if(eElement instanceof EntityLabelElement) {
					Set<String> labels = toLabelSet(propValue);
					Set<String> subdomain = ((EntityLabelElement) eElement).getSubdomain();
					Set<String> removedLabels = new LinkedHashSet<String>();
					Iterator<String> itrLabels = labels.iterator();
					while(itrLabels.hasNext()) {
						String label = itrLabels.next();
						if(!subdomain.contains(label)) {
							removedLabels.add(label);
							itrLabels.remove();
						}
					}
					String[] labelJoined = new String[labels.size()];
					labels.toArray(labelJoined);
					entity.putValue(propName, TextUtils.join(labelJoined));
					if(!removedLabels.isEmpty()) {
						logger.info("保存的label[name=" + propName + "]" + removedLabels + "不在配置的subdomain范围内" + ((EntityLabelElement) eElement).getSubdomain());
					}
				}
			}
		}
		return report;
		
	}

	

	private Set<String> toLabelSet(Object propValue) {
		Set<String> labels = new LinkedHashSet<String>();
		if(propValue != null ) {
			if(propValue.getClass().isArray()) {
				for(Object val : (Object[])propValue) {
					labels.addAll(splitToSet(FormatUtils.toString(val)));
				}
			}else if(propValue instanceof Collection) {
				for(Object val : (Collection<?>)propValue) {
					labels.addAll(splitToSet(FormatUtils.toString(val)));
				}
			}else if(propValue instanceof String) {
				String strval = (String) propValue;
				if(strval.contains(",")) {
					return toLabelSet(strval.split(","));
				}else {
					labels.add(strval);
				}
			}
		}
		return labels;
	}


	private Set<String> splitToSet(String val) {
		Set<String> result = new LinkedHashSet<>();
		if(val != null) {
			for(String snippet : val.split(",")) {
				result.add(snippet);
			}
		}
		return result;
	}


	private boolean filterEntityElement(EntityElement eElement, Object propValue) {
		if(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL.equals(eElement.getName()) && propValue instanceof String && !TextUtils.hasText((String) propValue)) {
			return false;
		}
		if(eElement instanceof EntityAttrElement && propValue != null) {
			if(propValue instanceof Collection || propValue.getClass().isArray()) {
				String s = FormatUtils.join(propValue, ",");
				if(s != null) {
					logger.info(eElement.getName() + "传入的参数类型为数组或者集合类型的数据[" + s + "]，系统无法确定保存的值，将跳过");
					return false;
				}
			}
		}
		return true;
	}


	protected boolean prevSetValue(String propName, Object propValue) {
		return false;
	}


	/**
	 * 根据直接属性名获得该属性的节点信息
	 * @param propName
	 * @return
	 */
	protected abstract EntityElement getEntityElement(String propName);
	
	@Override
	public void removeAllComposite(String compositeName) {
		SimpleEntity source = getEntity().getEntity();
		if(source instanceof Entity) {
			EntityElement element = getEntityElement(compositeName);
			if(element instanceof EntityMultiAttributeElement) {
				((Entity) source).removeAllMultiAttrEntity(compositeName);
			}else if(element instanceof EntityRelationElement) {
				((Entity) source).removeAllRelationEntity(compositeName);
			}
		}
	}
}
