package cn.sowell.datacenter.entityResolver;

import java.util.Set;

import com.abc.mapping.node.RelationNode;

import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;

public class RelationFieldConfigure extends AbstractFieldConfigure<RelationNode>{

	public RelationFieldConfigure(String mappingName, String absoluteName, RelationNode relationNode) {
		super(mappingName, absoluteName, relationNode);
	}

	public Set<String> getLabelDomain() throws UnconfiuredFusionException{
		return getNode().getLabelNode().getSubdomains();
	}
	
	public String getAbcNodeAbcAttr() {
		RelationNode n = getNode();
		return n.getAbcNode().getAbcattr();
	}

	@Override
	public String getFieldType() {
		return FieldConfigure.RELATION_FILED_TYPE;
	}



}
