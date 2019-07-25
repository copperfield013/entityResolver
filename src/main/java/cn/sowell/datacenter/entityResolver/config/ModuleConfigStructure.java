package cn.sowell.datacenter.entityResolver.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import cho.carbon.meta.struc.er.Field;
import cho.carbon.meta.struc.er.Group2D;
import cho.carbon.meta.struc.er.ItemElement;
import cho.carbon.meta.struc.er.RStruc;
import cho.carbon.meta.struc.er.Struc;
import cho.carbon.meta.struc.er.StrucContainer;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
@SuppressWarnings("unused")
public class ModuleConfigStructure {
	
	private Integer rootNodeMappingId;
	private Map<Integer, ABC> abcNodeMap = new HashMap<>();
	FusionContextConfigFactory fFactory;
	
	
	
	private ModuleConfigStructure(FusionContextConfig fusionContextConfig, Set<Module> allModules){
		Assert.notNull(fusionContextConfig);
		this.rootNodeMappingId = fusionContextConfig.getMappingId();
		Map<Integer, Module> mappingIdModuleMap = CollectionUtils.toMap(allModules, Module::getMappingId);
		traverseABC(fusionContextConfig.getMappingId(), fusionContextConfig.getRootNode(), mappingIdModuleMap);
	}
	
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("rootNodeMappingId", this.rootNodeMappingId);
		json.put("abcNodes", this.abcNodeMap.values());
		return json;
	}
	
	public Map<String, Map<String, String>> analyzeModuleRelationModuleMap(){
		Map<String, Map<String, String>> moduleRelationModuleMap = new HashMap<>();
		ABC root = this.abcNodeMap.get(this.rootNodeMappingId);
		this.abcNodeMap.values().forEach(abcNode->{
			Map<String, String> relationModuleMap = new HashMap<>();
			moduleRelationModuleMap.put(abcNode.getModuleName(), relationModuleMap);
			List<Rel> rels = abcNode.getRels();
			for (Rel rel : rels) {
				ABC rabc = this.abcNodeMap.get(rel.getRabcNodeMappingId());
				if(rabc != null) {
					relationModuleMap.put(rel.getName(), rabc.getModuleName());
				}
			}
		});
		
		return moduleRelationModuleMap;
	}
	

	static ModuleConfigStructure analyzeStructure(FusionContextConfig fusionContextConfig, Set<Module> allModules) {
		Assert.notNull(fusionContextConfig);
		ModuleConfigStructure structure = new ModuleConfigStructure(fusionContextConfig, allModules);
		return structure;
	}
	
	private ABC traverseABC(Integer mappingId, Struc nRootNode, Map<Integer, Module> mappingIdModuleMap) {
		Module module = mappingIdModuleMap.get(mappingId);
		if(module != null) {
			ABC abc = new ABC(module.getMappingId(), module.getName(), module.getTitle());
			Collection<Field> nAttrs = nRootNode.getAllField();
			abcNodeMap.put(mappingId, abc);
			for (Field nAttr : nAttrs) {
				Attr attr = new Attr();
				setNormalAttrs(attr, nAttr);
				abc.getAttrs().add(attr);
			}
			
			Collection<Group2D> nMattrs = nRootNode.getGroup2Ds();
			for (Group2D nMattr : nMattrs) {
				Mattr mattr = new Mattr();
				setNormalAttrs(mattr, nMattr);
				abc.getMattrs().add(mattr);
			}
			
			Collection<RStruc> nRels = nRootNode.getRStrucs();
			for (RStruc nRel : nRels) {
				if(nRel.getPointStruc() != null) {
					
					Struc rabcNode = nRel.getPointStruc();
					ABC rabc = null;
					Integer rabcNodeMappingId = rabcNode.getId();
					if(abcNodeMap.containsKey(rabcNodeMappingId)) {
						rabc = abcNodeMap.get(rabcNodeMappingId);
					}else {
						;
						rabc = traverseABC(rabcNodeMappingId, StrucContainer.findStruc(rabcNodeMappingId), mappingIdModuleMap);
					}
					
					Rel rel = new Rel(rabcNodeMappingId, rabc);
					Collection<String> relLabels = nRel.getRelationTypeNames();
					Assert.notNull(relLabels, "label为空");
					rel.getLabels().addAll(relLabels);
					setNormalAttrs(rel, nRel);
					abc.getRels().add(rel);
				}
			}
			return abc;
		}
		return null;
	}


	private void setNormalAttrs(Named attr, ItemElement nAttr) {
		attr.setName(nAttr.getTitle());
		attr.setAbcattr(nAttr.getItemCode());
		//attr.setFullName(nAttr.getFullTitle());
	}


	private static class ABC{
		private Integer mappingId;
		private String moduleName;
		public ABC(Integer mappingId, String moduleName, String moduleTitle) {
			super();
			this.mappingId = mappingId;
			this.moduleName = moduleName;
			this.moduleTitle = moduleTitle;
		}
		private String moduleTitle;
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
		public Integer getMappingId() {
			return mappingId;
		}
		public String getModuleName() {
			return moduleName;
		}
		public String getModuleTitle() {
			return moduleTitle;
		}
	}
	
	private static abstract class Named{
		private String name;
		private String abcattr;
		//private String fullName;

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

		/*
		 * public String getFullName() { return fullName; }
		 * 
		 * public void setFullName(String fullName) { this.fullName = fullName; }
		 */
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
		private Set<String> labels = new LinkedHashSet<>();
		private Integer rabcNodeMappingId;
		@JSONField(serialize=false)
		private ABC rabc;

		public Rel(Integer rabcNodeMappingId, ABC rabc) {
			super();
			this.rabcNodeMappingId = rabcNodeMappingId;
			this.rabc = rabc;
		}

		public ABC getRabc() {
			return rabc;
		}

		public Integer getRabcNodeMappingId() {
			return rabcNodeMappingId;
		}

		public Set<String> getLabels() {
			return labels;
		}

		
	}

	public Map<Integer, ABC> getAbcNodeMap() {
		return abcNodeMap;
	}


	public Integer getRootNodeMappingId() {
		return rootNodeMappingId;
	}


}
