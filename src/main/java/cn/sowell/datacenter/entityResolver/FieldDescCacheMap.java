package cn.sowell.datacenter.entityResolver;

import java.util.HashMap;
import java.util.Map;

public class FieldDescCacheMap extends GetonlyMap<Long, FieldParserDescription>{

	Map<Long, FieldParserDescription> cache = new HashMap<>();
	private FusionContextConfigResolver resolver;
	public FieldDescCacheMap(FusionContextConfigResolver resolver) {
		super();
		this.resolver = resolver;
	}
	@Override
	public FieldParserDescription get(Object fieldId) {
		if(!cache.containsKey(fieldId)) {
			cache.put((Long) fieldId, resolver.getFieldParserDescription((Long) fieldId));
		}
		return cache.get(fieldId);
	}
}
