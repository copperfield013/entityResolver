package cn.sowell.datacenter.entityResolver;

import java.util.Map;

import cn.sowell.copframe.utils.FormatUtils;

public class FieldParserDescription {
	private Field field;
	
	public FieldParserDescription(Field field) {
		super();
		this.field = field;
	}

	public Long getFieldId() {
		return field.getId();
	}
	
	public String getFieldTitle() {
		return field.getTitle();
	}
	
	public String getAbcType() {
		return field.getAbcType();
	}
	
	public String getFullKey() {
		return field.getFullKey();
	}
	
	public static final String INDEX_REPLACEMENT = "ARRAY_INDEX_REPLACEMENT";
	public String getArrayFieldNameFormat() {
		String suffix = field.getFullKey();
		Composite composite = field.getComposite();
		if(composite != null && composite.getIsArray() != null) {
			String compositeName = composite.getName();
			if(field.getFullKey().startsWith(compositeName)) {
				suffix = field.getFullKey().substring(compositeName.length());
				if(!suffix.matches("^\\[\\d+\\].+$")) {
					suffix = "[" + INDEX_REPLACEMENT + "]" + suffix;
				}
				suffix = compositeName + suffix;
			}
		}
		return suffix;
	}
	
	public String getArrayFieldNameFormat(int index) {
		String format = getArrayFieldNameFormat();
		return format.replace(INDEX_REPLACEMENT, String.valueOf(index));
	}
	
	private Map<String, String> arrayFieldNameMap = new GetonlyMap<String, String>() {

		@Override
		public String get(Object key) {
			return getArrayFieldNameFormat(FormatUtils.toInteger(key));
		}
	};
	
	
	public Map<String, String> getArrayFieldNameMap() {
		return arrayFieldNameMap;
	}
	
	
}
