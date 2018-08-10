package cn.sowell.datacenter.entityResolver.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.abc.mapping.conf.MappingContainer;
import com.abc.mapping.entity.Entity;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.AttributeNode;
import com.abc.mapping.node.LabelNode;
import com.abc.mapping.node.MultiAttributeNode;
import com.abc.mapping.node.RelationNode;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.ImportCompositeField;
import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy.NodeSwitch;

public class ABCNodeFusionContextConfigResolver extends AbstractFusionContextConfigResolver{

	private ABCNode rootNode;
	
	public ABCNodeFusionContextConfigResolver(FusionContextConfig config) {
		super(config);
		try {
			rootNode = MappingContainer.getABCNode(config.getMappingName());
		} catch (Exception e) {
			throw new RuntimeException("初始化ABC配置时发生错误[" + config.getMappingName() + "]", e);
		}
		if(rootNode == null) {
			throw new RuntimeException("没有找到ABC配置[" + config.getMappingName() + "]");
		}
	}

	@Override
	protected EntityBindContext buildRootContext(Entity entity) {
		return new ABCNodeEntityBindContext(rootNode, entity);
	}
	
	@Override
	public FieldConfigure getFieldConfigure(String fieldPath) {
		StringBuffer absolutePath = new StringBuffer();
		ABCNodeProxy fieldProxy = PropertyNamePartitions.split(fieldPath, new ABCNodeProxy(rootNode), (snippet, eleProxy)->{
			absolutePath.append(snippet.getPartitions().getMainPartition() + ".");
			return eleProxy.getElement(snippet.getPartitions().getMainPartition());
		});
		if(absolutePath.length() > 0 && '.' == absolutePath.charAt(absolutePath.length() - 1)) {
			absolutePath.deleteCharAt(absolutePath.length() - 1);
		}
		if(fieldProxy != null) {
			return (FieldConfigure) fieldProxy.doByNodeSwitch(new NodeSwitch<Object>() {
				@Override
				public Object handlerWithNode(RelationNode node, Object arg) {
					return new RelationFieldConfigure(config.getMappingName(), absolutePath.toString(), node);
				}
			}, null);
		}else {
			return null;
		}
	}

	public Set<Label> getAllLabels() {
		HashSet<Label> labels = new HashSet<Label>();
		Map<String, LabelNode> nodeMap = getElements(rootNode, "", 
				node->node.getLabels(), 
				(itemMap, param)->itemMap.put(param.getPrefix() + param.getRelationNode().getTitle(), param.getRelationNode().getLabelNode()), 
				null);
		CollectionUtils.appendTo(nodeMap.entrySet(), labels, node->{
			return new Label() {
				
				@Override
				public Set<String> getSubdomain() {
					return node.getValue().getSubdomains();
				}
				
				@Override
				public String getFieldName() {
					return node.getKey();
				}
				
				@Override
				public FusionContextConfig getConfig() {
					return config;
				}
			};
		});
		return labels;
	}

	private static class RelationHandlerParam{
		final private RelationNode relationNode;
		final private String prefix;
		public RelationHandlerParam(RelationNode relationNode, String prefix) {
			super();
			this.relationNode = relationNode;
			this.prefix = prefix;
		}
		public RelationNode getRelationNode() {
			return relationNode;
		}
		public String getPrefix() {
			return prefix;
		}
		
	}
	
	private static class MultiAttributeHandlerParam{
		final private MultiAttributeNode multiAttributeNode;
		final private String prefix;
		public MultiAttributeNode getMultiAttributeNode() {
			return multiAttributeNode;
		}
		public String getPrefix() {
			return prefix;
		}
		public MultiAttributeHandlerParam(MultiAttributeNode multiAttributeNode, String prefix) {
			super();
			this.multiAttributeNode = multiAttributeNode;
			this.prefix = prefix;
		}
	}
	
	private <T extends AttributeNode> Map<String, T> getElements(ABCNode node, String prefix, 
			Function<ABCNode, List<T>> itemGetter, 
			BiConsumer<Map<String, T>, RelationHandlerParam> relationHandler, 
			BiConsumer<Map<String, T>, MultiAttributeHandlerParam> multipleHandler) {
		Map<String, T> itemMap = new LinkedHashMap<>();
		List<T> items = itemGetter.apply(node);
		if(items != null) {
			for (T item : items) {
				itemMap.put(prefix + item.getTitle(), item);
			}
		}
		Collection<RelationNode> relations = node.getRelation();
		if(relations != null) {
			for (RelationNode relation : relations) {
				if(relationHandler != null) {
					relationHandler.accept(itemMap, new RelationHandlerParam(relation, prefix));
				}
				itemMap.putAll(getElements(relation.getAbcNode(), relation.getTitle() + ".", itemGetter, relationHandler, multipleHandler));
			}
		}
		if(multipleHandler != null) {
			Collection<MultiAttributeNode> multis = node.getMultiAttributes();
			for (MultiAttributeNode multi : multis) {
				multipleHandler.accept(itemMap, new MultiAttributeHandlerParam(multi, prefix));
			}
		}
		return itemMap;
	}

	public Set<ImportCompositeField> getAllImportFields() {
		Set<String> relationNames = new HashSet<String>();
		Map<String, AttributeNode> map = getElements(rootNode, "", node->{
			List<AttributeNode> nodes = new ArrayList<>();
			nodes.addAll(node.getAttributes());
			nodes.addAll(node.getLabels());
			return nodes;
		}, (itemMap, param)->relationNames.add(param.getPrefix() + param.getRelationNode().getTitle()), (itemMap, param)->{
			itemMap.put(param.getPrefix() + param.getMultiAttributeNode().getTitle(), param.getMultiAttributeNode());
		});
		
		return CollectionUtils.toSet(map.entrySet(), enitry->
			new ImportCompositeField() {
				
				@Override
				public boolean getIsMultipleField() {
					return enitry.getKey().contains(".");
				}
				
				@Override
				public String getFieldNamePattern() {
					if(getIsMultipleField()) {
						StringBuffer buffer = new StringBuffer(getFieldName());
						buffer.insert(buffer.lastIndexOf("."), "[" + ImportCompositeField.REPLACE_INDEX + "]");
						return buffer.toString();
					}else {
						return getFieldName();
					}
				}
				
				@Override
				public String getFieldName() {
					return enitry.getKey();
				}
				
				@Override
				public String getRelationKey() {
					String fieldName = getFieldName();
					return relationNames.stream()
						.filter(name->fieldName.startsWith(name + "."))
						.findFirst().orElse(null);
					
				}
				
			}
		);
	}


}

