package cn.sowell.datacenter.entityResolver;

import java.util.HashMap;
import java.util.Map;

import cn.sowell.copframe.utils.FormatUtils;

public class FieldDescCacheMap extends GetonlyMap<Long, FieldParserDescription>{

	Map<Integer, FieldParserDescription> cache = new HashMap<>();
	private FusionContextConfigResolver resolver;
	public FieldDescCacheMap(FusionContextConfigResolver resolver) {
		super();
		this.resolver = resolver;
	}
	@Override
	public FieldParserDescription get(Object fieldId) {
		Integer intFieldId = FormatUtils.toInteger(fieldId);
		if(!cache.containsKey(intFieldId)) {
			cache.put(intFieldId, resolver.getFieldParserDescription(intFieldId));
		}
		return cache.get(fieldId);
	}
}
