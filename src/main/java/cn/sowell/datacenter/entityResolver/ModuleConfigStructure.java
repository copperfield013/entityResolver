package cn.sowell.datacenter.entityResolver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.abc.mapping.conf.MappingContainer;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.AttributeNode;
import com.abc.mapping.node.MultiAttributeNode;
import com.abc.mapping.node.RABCNode;
import com.abc.mapping.node.RelationNode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.copframe.utils.FormatUtils;
@SuppressWarnings("unused")
public class ModuleConfigStructure {
	
	private Long rootNodeMappingId;
	private Map<Long, ABC> abcNodeMap = new HashMap<>();
	
	private ModuleConfigStructure(FusionContextConfig fusionContextConfig){
		Assert.notNull(fusionContextConfig);
		this.rootNodeMappingId = fusionContextConfig.getMappingId();
		traverseABC(fusionContextConfig.getMappingId(), fusionContextConfig.getRootNode());
	}
	
	
	public JSONObject toJson() {
		return (JSONObject) JSON.toJSON(this);
	}
	
	
	static ModuleConfigStructure analyzeStructure(FusionContextConfig fusionContextConfig) {
		Assert.notNull(fusionContextConfig);
		ModuleConfigStructure structure = new ModuleConfigStructure(fusionContextConfig);
		return structure;
	}
	
	private ABC traverseABC(Long mappingId, ABCNode nRootNode) {
		ABC abc = new ABC();
		Collection<AttributeNode> nAttrs = nRootNode.getAttributes();
		abcNodeMap.put(mappingId, abc);
		for (AttributeNode nAttr : nAttrs) {
			Attr attr = new Attr();
			setNormalAttrs(attr, nAttr);
			abc.getAttrs().add(attr);
		}
		
		Collection<MultiAttributeNode> nMattrs = nRootNode.getMultiAttributes();
		for (MultiAttributeNode nMattr : nMattrs) {
			Mattr mattr = new Mattr();
			setNormalAttrs(mattr, nMattr);
			abc.getMattrs().add(mattr);
		}
		
		Collection<RelationNode> nRels = nRootNode.getRelation();
		for (RelationNode nRel : nRels) {
			if(nRel.getRabcNode() != null) {
				RABCNode rabcNode = nRel.getRabcNode();
				ABC rabc = null;
				Long rabcNodeMappingId = FormatUtils.toLong(rabcNode.getRelABCNodeID());
				if(abcNodeMap.containsKey(rabcNodeMappingId)) {
					rabc = abcNodeMap.get(rabcNodeMappingId);
				}else {
					rabc = traverseABC(rabcNodeMappingId, MappingContainer.getABCNode(BigInteger.valueOf(rabcNodeMappingId)));
				}
				
				Rel rel = new Rel(rabcNodeMappingId, rabc);
				setNormalAttrs(rel, nRel);
				abc.getRels().add(rel);
			}
		}
		return abc;
	}


	private void setNormalAttrs(Named attr, AttributeNode nAttr) {
		attr.setName(nAttr.getTitle());
		attr.setAbcattr(nAttr.getAbcattr());
		attr.setFullName(nAttr.getFullTitle());
	}


	private static class ABC{
		private List<Attr> attrs = new ArrayList<>();
		private List<Mattr> mattrs = new ArrayList<>();
		private List<Rel> rels = new ArrayList<>();
		public List<Attr> getAttrs() {
			return attrs;
		}
		public List<Mattr> getMattrs() {
			return mattrs;
		}
		public List<Rel> getRels() {
			return rels;
		}
	}
	
	private static abstract class Named{
		private String name;
		private String abcattr;
		private String fullName;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAbcattr() {
			return abcattr;
		}

		public void setAbcattr(String abcattr) {
			this.abcattr = abcattr;
		}

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
	}
	
	private static class Attr extends Named{
	}
	
	private static class Mattr extends Named{
		private List<Attr> attrs;

		public List<Attr> getAttrs() {
			return attrs;
		}

		public void setAttrs(List<Attr> attrs) {
			this.attrs = attrs;
		}
	}
	
	private static class Rel extends Named{
		private Long rabcNodeMappingId;
		@JSONField(serialize=false)
		private ABC rabc;

		public Rel(Long rabcNodeMappingId, ABC rabc) {
			super();
			this.rabcNodeMappingId = rabcNodeMappingId;
			this.rabc = rabc;
		}

		public ABC getRabc() {
			return rabc;
		}

		public Long getRabcNodeMappingId() {
			return rabcNodeMappingId;
		}

		
	}

	public Map<Long, ABC> getAbcNodeMap() {
		return abcNodeMap;
	}


	public Long getRootNodeMappingId() {
		return rootNodeMappingId;
	}


}
