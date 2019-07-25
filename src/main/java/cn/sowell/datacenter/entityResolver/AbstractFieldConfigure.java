package cn.sowell.datacenter.entityResolver;

import cho.carbon.meta.struc.er.ItemElement;

public abstract class AbstractFieldConfigure<T extends ItemElement> implements FieldConfigure{
	
	private final T node;
	private final String absoluteName;
	private final Integer mappingId;

	public AbstractFieldConfigure(Integer mappingId, String absoluteName, T relationNode) {
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
		return node.getItemCode();
	}

	@Override
	public Integer getMappingId() {
		return this.mappingId;
	}
	

}
