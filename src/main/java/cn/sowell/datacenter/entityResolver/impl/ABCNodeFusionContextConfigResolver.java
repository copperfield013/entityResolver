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

import org.apache.log4j.Logger;

import com.abc.mapping.entity.RecordEntity;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.AttributeNode;
import com.abc.mapping.node.LabelNode;
import com.abc.mapping.node.MultiAttributeNode;
import com.abc.mapping.node.RelationNode;
import com.abc.model.enun.NodeOpsType;
import com.beust.jcommander.internal.Lists;

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

	static Logger logger = Logger.getLogger(ABCNodeFusionContextConfigResolver.class);
	
	
	public ABCNodeFusionContextConfigResolver(FusionContextConfig config) {
		super(config);
		if(config.getRootNode() == null) {
			throw new RuntimeException("没有找到ABC配置[mappingId=" + config.getMappingId() + "]");
		}
	}
	
	ABCNode getRootNode(){
		return this.config.getRootNode();
	}

	@Override
	protected EntityBindContext buildRootContext(RecordEntity entity) {
		return new ABCNodeEntityBindContext(getRootNode(), entity);
	}
	
	@Override
	public FieldConfigure getFieldConfigure(String fieldPath) {
		StringBuffer absolutePath = new StringBuffer();
		ABCNodeProxy fieldProxy = PropertyNamePartitions.split(fieldPath, new ABCNodeProxy(getRootNode()), (snippet, eleProxy)->{
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
					return new RelationFieldConfigure(config.getMappingId(), absolutePath.toString(), node);
				}
			}, null);
		}else {
			return null;
		}
	}

	public Set<Label> getAllLabels() {
		HashSet<Label> labels = new HashSet<Label>();
		Map<String, LabelNode> nodeMap = getElements(getRootNode(), "", 
				node->node.getLabels(), 
				null,
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
	
	@SuppressWarnings("unused")
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
	
	private <T extends AttributeNode> Map<String, AttributeNode> getElements(MultiAttributeNode node, String prefix, Function<MultiAttributeNode, List<T>> multipleChildrenGetter){
		Map<String, AttributeNode> itemMap = new LinkedHashMap<>();
		if(multipleChildrenGetter != null) {
			Collection<T> items = multipleChildrenGetter.apply(node);
			if(items != null) {
				for (AttributeNode item : items) {
					itemMap.put(prefix + item.getTitle(), item);
				}
			}
		}
		return itemMap;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends AttributeNode> Map<String, T> getElements(ABCNode node, String prefix, 
			Function<ABCNode, List<T>> itemGetter, 
			Function<MultiAttributeNode, List<T>> multipleChildrenGetter, 
			BiConsumer<Map<String, T>, RelationHandlerParam> relationHandler, 
			BiConsumer<Map<String, T>, MultiAttributeHandlerParam> multipleHandler) {
		Map<String, T> itemMap = new LinkedHashMap<>();
		if(itemGetter != null) {
			List<T> items = itemGetter.apply(node);
			if(items != null) {
				for (T item : items) {
					itemMap.put(prefix + item.getTitle(), item);
				}
			}
		}
		Collection<RelationNode> relations = ((ABCNode) node).getRelation();
		if(relations != null) {
			for (RelationNode relation : relations) {
				if(relationHandler != null) {
					relationHandler.accept(itemMap, new RelationHandlerParam(relation, prefix));
				}
				itemMap.putAll(getElements(relation.getAbcNode(), relation.getTitle() + ".", itemGetter, multipleChildrenGetter, relationHandler, multipleHandler));
			}
		}
		Collection<MultiAttributeNode> multiples = ((ABCNode) node).getMultiAttributes();
		if(multiples != null && multipleChildrenGetter != null) {
			for (MultiAttributeNode multiple : multiples) {
				if(multipleHandler != null) {
					multipleHandler.accept(itemMap, new MultiAttributeHandlerParam(multiple, prefix));
				}
				itemMap.putAll((Map<String, ? extends T>) getElements(multiple, prefix, multipleChildrenGetter));
			}
		}
		return itemMap;
	}

	public Set<String> getAllRelationNames(){
		Set<String> relationNames = new HashSet<String>();
		getElements(getRootNode(), "", null, null, 
			(itemMap, param)->relationNames.add(param.getPrefix() + param.getRelationNode().getTitle()), 
			null);
		return relationNames;
	}
	
	public Set<ImportCompositeField> getAllImportFields() {
		Set<String> relationNames = new HashSet<String>();
		Map<String, AttributeNode> map = getElements(getRootNode(), "", node->{
			List<AttributeNode> nodes = new ArrayList<>();
			nodes.addAll(node.getAttributes());
			nodes.addAll(node.getLabels());
			return nodes;
		}, mnode->Lists.newArrayList(mnode.getAttributes()), 
			(itemMap, param)->relationNames.add(param.getPrefix() + param.getRelationNode().getTitle()), 
			null);
		
		return CollectionUtils.toSet(map.entrySet(), entry->{
			ImportCompositeField f = new ImportCompositeField() {
				
				@Override
				public boolean getIsMultipleField() {
					return entry.getKey().contains(".");
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
					return entry.getKey();
				}
				
				@Override
				public String getRelationKey() {
					String fieldName = getFieldName();
					return relationNames.stream()
						.filter(name->fieldName.startsWith(name + "."))
						.findFirst().orElse(null);
				}
				
			};
			return f;
		});
	}

	public NodeOpsType getABCNodeAccess() {
		return getRootNode().getOpsType();
	}
	
	@Override
	public boolean isEntityWritable() {
		NodeOpsType nodeAccess = getABCNodeAccess();
		if(NodeOpsType.READ.equals(nodeAccess)) {
			return false;
		}
		return true;
	}
}

