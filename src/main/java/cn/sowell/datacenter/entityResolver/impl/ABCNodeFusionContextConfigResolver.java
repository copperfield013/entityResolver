package cn.sowell.datacenter.entityResolver.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.beust.jcommander.internal.Lists;

import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.meta.enun.StrucOptType;
import cho.carbon.meta.struc.er.Field;
import cho.carbon.meta.struc.er.Group2D;
import cho.carbon.meta.struc.er.RStruc;
import cho.carbon.meta.struc.er.Struc;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
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
	
	Struc getRootNode(){
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
				public Object handlerWithNode(RStruc node, Object arg) {
					return new RelationFieldConfigure(config.getMappingId(), absolutePath.toString(), node);
				}
			}, null);
		}else {
			return null;
		}
	}

	public Set<Label> getAllLabels() {
		HashSet<Label> labels = new HashSet<Label>();
		Map<String, Field> nodeMap = getElements(getRootNode(), "", node->node.getAllField().stream().filter(field->field.getEnumId() != null && field.getEnumId() > 0).collect(Collectors.toList()), null, null, null, new HashMap<>());
		Set<String> emptySet = Collections.unmodifiableSet(new HashSet<String>());
		CollectionUtils.appendTo(nodeMap.entrySet(), labels, node->{
			FieldParserDescription fieldDesc = getFieldParserDescription(node.getValue().getId());
			return new Label() {
				
				@Override
				public Set<String> getSubdomain() {
					return FormatUtils.coalesce(fieldDesc.getLabels(), emptySet);
					//return node.getValue();
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
		final private RStruc relationNode;
		final private String prefix;
		public RelationHandlerParam(RStruc relationNode, String prefix) {
			super();
			this.relationNode = relationNode;
			this.prefix = prefix;
		}
		public RStruc getRelationNode() {
			return relationNode;
		}
		public String getPrefix() {
			return prefix;
		}
		
	}
	
	@SuppressWarnings("unused")
	private static class MultiAttributeHandlerParam{
		final private Group2D multiAttributeNode;
		final private String prefix;
		public Group2D getMultiAttributeNode() {
			return multiAttributeNode;
		}
		public String getPrefix() {
			return prefix;
		}
		public MultiAttributeHandlerParam(Group2D multiAttributeNode, String prefix) {
			super();
			this.multiAttributeNode = multiAttributeNode;
			this.prefix = prefix;
		}
	}
	
	private <T extends Field> Map<String, Field> getElements(Group2D node, String prefix, Function<Group2D, List<T>> multipleChildrenGetter){
		Map<String, Field> itemMap = new LinkedHashMap<>();
		if(multipleChildrenGetter != null) {
			Collection<T> items = multipleChildrenGetter.apply(node);
			if(items != null) {
				for (Field item : items) {
					itemMap.put(prefix + item.getTitle(), item);
				}
			}
		}
		return itemMap;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Field> Map<String, T> getElements(Struc node, String prefix, 
			Function<Struc, List<T>> itemGetter, 
			Function<Group2D, List<T>> multipleChildrenGetter, 
			BiConsumer<Map<String, T>, RelationHandlerParam> relationHandler, 
			BiConsumer<Map<String, T>, MultiAttributeHandlerParam> multipleHandler,
			Map<Struc, Map<String, T>> cachedStrucItemMap) {
		if(cachedStrucItemMap.containsKey(node)) {
			return cachedStrucItemMap.get(node);
		}
		Map<String, T> itemMap = new LinkedHashMap<>();
		cachedStrucItemMap.put(node, itemMap);
		if(itemGetter != null) {
			List<T> items = itemGetter.apply(node);
			if(items != null) {
				for (T item : items) {
					itemMap.put(prefix + item.getTitle(), item);
				}
			}
		}
		Collection<RStruc> relations = ((Struc) node).getRStrucs();
		if(relations != null) {
			for (RStruc relation : relations) {
				if(relationHandler != null) {
					relationHandler.accept(itemMap, new RelationHandlerParam(relation, prefix));
				}
				if(relation.getPointStruc() != null) {
					itemMap.putAll(getElements(relation.getPointStruc(), relation.getTitle() + ".", itemGetter, multipleChildrenGetter, relationHandler, multipleHandler, cachedStrucItemMap));
				}else {
					itemMap.putAll(getElements(relation, relation.getTitle() + ".", itemGetter, multipleChildrenGetter, relationHandler, multipleHandler, cachedStrucItemMap));
				}
			}
		}
		Collection<Group2D> multiples = ((Struc) node).getGroup2Ds();
		if(multiples != null && multipleChildrenGetter != null) {
			for (Group2D multiple : multiples) {
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
			null, new HashMap<>());
		return relationNames;
	}
	
	public Set<ImportCompositeField> getAllImportFields() {
		Set<String> relationNames = new HashSet<String>();
		Map<String, Field> map = getElements(getRootNode(), "", node->{
			List<Field> nodes = new ArrayList<>();
			nodes.addAll(node.getAllField());
			return nodes;
		}, mnode->Lists.newArrayList(mnode.getAllField()), 
			(itemMap, param)->relationNames.add(param.getPrefix() + param.getRelationNode().getTitle()), 
			null, new HashMap<>());
		
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

	public StrucOptType getABCNodeAccess() {
		return getRootNode().getOpt();
	}
	
	@Override
	public boolean isEntityWritable() {
		StrucOptType nodeAccess = getABCNodeAccess();
		if(StrucOptType.READ.equals(nodeAccess)) {
			return false;
		}
		return true;
	}
}

