package cn.sowell.datacenter.entityResolver;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sowell.copframe.utils.Assert;
import cn.sowell.copframe.utils.FormatUtils;

public class PropertyNamePartitions {
	
	static Pattern pattern = Pattern.compile("^([^\\[\\]\\s]+)(\\[(\\d+)\\])?$");
	
	private String mainPartition;
	private Integer index;
	private int defaultIndex = 0;
	public static class PropertyNamePartitionsComposite{
		private final PropertyNamePartitions partitions;
		private final int totalCount;
		private final int index;
		public PropertyNamePartitionsComposite(PropertyNamePartitions partitions, int index,
				int totalCount) {
			super();
			Assert.notNull(partitions);
			this.partitions = partitions;
			this.totalCount = totalCount;
			this.index = index;
		}
		public PropertyNamePartitions getPartitions() {
			return partitions;
		}
		public boolean isFirst() {
			return getIndex() == 0;
		}
		public boolean isLast() {
			return getTotalCount() - 1 == index;
		}
		public int getIndex() {
			return index;
		}
		public int getTotalCount() {
			return totalCount;
		}
	}
	public static <T> T split(String str, T arg, BiFunction<PropertyNamePartitionsComposite, T, T> func) {
		Assert.notNull(func);
		String[] split = str.split("\\.");
		for (int index = 0; index < split.length; index++) {
			String snippet = split[index];
			arg = func.apply(new PropertyNamePartitionsComposite(new PropertyNamePartitions(snippet), index, split.length), arg);
		}
		return arg;
	}
	
	public PropertyNamePartitions(String propName) {
		Matcher matcher = pattern.matcher(propName);
		if(matcher.matches()) {
			mainPartition = matcher.group(1);
			index = FormatUtils.toInteger(matcher.group(3));
		}else {
			throw new RuntimeException("字段名称格式不正确[" + propName + "]");
		}
	}

	public String getMainPartition() {
		return mainPartition;
	}

	public Integer getIndex() {
		return index == null? getDefaultIndex(): index;
	}

	public int getDefaultIndex() {
		return defaultIndex;
	}

	public void setDefaultIndex(int defaultIndex) {
		this.defaultIndex = defaultIndex;
	}

}

