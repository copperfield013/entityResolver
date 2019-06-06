package cn.sowell.datacenter.entityResolver;

import java.util.Map;

import com.abc.model.enun.AttributeValueType;

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
	
	public AttributeValueType getAbcType() {
		return AttributeValueType.valueOf(field.getAbcType());
		//return field.getAbcType();
	}
	
	public String getFullKey() {
		return field.getFullKey();
	}
	
	public static final String INDEX_REPLACEMENT = "ARRAY_INDEX_REPLACEMENT";
	public static String getArrayFieldNameFormat(String fieldName, String compositeName) {
		String suffix = fieldName;
		if(fieldName.startsWith(compositeName)) {
			suffix = fieldName.substring(compositeName.length());
			if(!suffix.matches("^\\[\\d+\\].+$")) {
				suffix = "[" + INDEX_REPLACEMENT + "]" + suffix;
			}
			suffix = compositeName + suffix;
		}
		return suffix;
	}
	public String getArrayFieldNameFormat() {
		Composite composite = field.getComposite();
		if(composite != null && composite.getIsArray() != null) {
			return getArrayFieldNameFormat(field.getFullKey(), composite.getName());
		}
		return field.getFullKey();
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
