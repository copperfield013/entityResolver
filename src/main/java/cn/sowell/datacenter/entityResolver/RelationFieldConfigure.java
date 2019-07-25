package cn.sowell.datacenter.entityResolver;

import java.util.LinkedHashSet;
import java.util.Set;

import cho.carbon.meta.struc.er.RStruc;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;

public class RelationFieldConfigure extends AbstractFieldConfigure<RStruc>{

	public RelationFieldConfigure(Integer mappingId, String absoluteName, RStruc relationNode) {
		super(mappingId, absoluteName, relationNode);
	}

	public Set<String> getLabelDomain() throws UnconfiuredFusionException{
		return new LinkedHashSet<String>(getNode().getRelationTypeNames());
	}
	
	public String getAbcNodeAbcAttr() {
		RStruc n = getNode();
		return n.getItemCode();
	}

	@Override
	public String getFieldType() {
		return FieldConfigure.RELATION_FILED_TYPE;
	}
	
	public Long getRabcMappingId() {
		RStruc n = getNode();
		if(n.getPointStruc() != null) {
			return FormatUtils.toLong(n.getPointStruc().getId());
		}
		return null;
	}



}
