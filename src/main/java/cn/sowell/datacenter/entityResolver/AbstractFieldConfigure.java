package cn.sowell.datacenter.entityResolver;

import com.abc.mapping.node.AttributeNode;

public abstract class AbstractFieldConfigure<T extends AttributeNode> implements FieldConfigure{
	
	private final T node;
	private final String absoluteName;
	private final Long mappingId;

	public AbstractFieldConfigure(Long mappingId, String absoluteName, T relationNode) {
		this.absoluteName = absoluteName;
		this.node = relationNode;
		this.mappingId = mappingId;
	}

	protected T getNode() {
		return node;
	}

	@Override
	public String getThisName() {
		return node.getTitle();
	}

	@Override
	public String getParentName() {
		if(absoluteName.contains(".")) {
			String[] split = absoluteName.split("\\.");
			return split[split.length - 2];
		}
		return null;
	}

	@Override
	public String getAbsoluteName() {
		return absoluteName;
	}


	@Override
	public String getAbcAttr() {
		return node.getAbcattrName();
	}

	@Override
	public Long getMappingId() {
		return this.mappingId;
	}
	

}
