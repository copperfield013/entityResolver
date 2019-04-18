package cn.sowell.datacenter.entityResolver.impl;

import java.util.HashSet;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.SimpleEntity;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.AttributeNode;
import com.abc.mapping.node.IAttributeNode;
import com.abc.mapping.node.LabelNode;
import com.abc.mapping.node.MultiAttributeNode;
import com.abc.mapping.node.RelationNode;
import com.abc.mapping.node.impl.AttributeNodeImpl;
import com.abc.model.constant.AttributeMatedata;
import com.abc.model.enun.ValueType;

import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.EntityProxy;

public class ABCNodeProxy {
	public static final String CODE_NODE_NAME = AttributeMatedata.PCOL_RECORDCODE;
	public static final String CODE_PROPERTY_NAME_NORMAL = "唯一编码";

	public static final String UPDATETIME_PROPERTY_NAME = "编辑时间";

	private Object node;
	
	public ABCNodeProxy(Object node) {
		INIT.handler(node, this);
	}
	
	/**
	 * 获得当前节点的子节点
	 * @param propertyName
	 * @return
	 */
	public ABCNodeProxy getElement(String propertyName) {
		Object ele = ELEMENT_GETTER.handler(node, propertyName);
		if(ele instanceof ABCNodeProxy) {
			return (ABCNodeProxy) ele;
		}else if(ele != null) {
			return new ABCNodeProxy(ele);
		}else if(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL.equals(propertyName)){
			if(node instanceof ABCNode || node instanceof MultiAttributeNode || node instanceof RelationNode) {
				IAttributeNode codeNode = new AttributeNodeImpl();
				codeNode.setName(CODE_PROPERTY_NAME_NORMAL);
				codeNode.setAbcattr(CODE_NODE_NAME);
				codeNode.setDatatype(ValueType.STRING);
				return new ABCNodeProxy(codeNode);
			}
		}
		return null;
	}
	
	/**
	 * 获得节点信息对象
	 * @return
	 */
	public EntityElement getEntityElement() {
		return (EntityElement) ENTITY_ELEMENT_GETTER.handler(node, null);
	}
	
	public EntityProxy createElementEntity() {
		return (EntityProxy) ELEMENT_ENTITY_CREATOR.handler(node, null);
	}
	
	
	protected void setNode(Object node) {
		this.node = node;
	}
	
	public <T> Object doByNodeSwitch(NodeSwitch<T> swit, T arg) {
		return swit.handler(this.node, arg);
	}
	
	public static interface NodeSwitch<T>{
		
		public default Object handler(Object node, T arg) {
			Object result = null;
			if(node instanceof ABCNode) {
				result = handlerWithNode((ABCNode)node, arg);
			}else if(node instanceof MultiAttributeNode) {
				result = handlerWithNode((MultiAttributeNode)node, arg);
			}else if(node instanceof RelationNode) {
				result = handlerWithNode((RelationNode)node, arg);
			}else if(node instanceof LabelNode) {
				result = handlerWithNode((LabelNode)node, arg);
			}else if(node instanceof AttributeNode) {
				result = handlerWithNode((AttributeNode)node, arg);
			}else {
				result = handlerWithOther(node, arg);
			}
			Object globalResult = handlerGlobal(node, arg);
			return result == null? globalResult: result;
		}
		public default Object handlerWithOther(Object node, T arg) { throw new RuntimeException("无法识别的标签对象");}
		public default Object handlerGlobal(Object node, T arg) {return null;}
		public default Object handlerWithNode(ABCNode node, T arg) {return null;};
		public default Object handlerWithNode(MultiAttributeNode node, T arg) {return null;};
		public default Object handlerWithNode(RelationNode node, T arg) {return null;};
		public default Object handlerWithNode(AttributeNode node, T arg) {return null;};
		public default Object handlerWithNode(LabelNode node, T arg) {return null;};
	}

	/**
	 * 初始化节点代理对象
	 */
	static NodeSwitch<ABCNodeProxy> INIT = new NodeSwitch<ABCNodeProxy>() {
		public Object handlerGlobal(Object node, ABCNodeProxy proxy) {
			proxy.setNode(node);
			return 1;
		};
	};
	
	/**
	 * 子节点获得的方式
	 */
	static NodeSwitch<String> ELEMENT_GETTER = new NodeSwitch<String>() {
		public Object handlerWithNode(ABCNode node, String propertyName) {
			return FormatUtils.coalesce(
					node.getAttribute(propertyName),
					node.getLabel(propertyName),
					node.getMultiAttribute(propertyName),
					node.getRelation(propertyName)
					);
		}
		public Object handlerWithNode(MultiAttributeNode node, String propertyName) {
			return node.getAttribute(propertyName);
		}
		public Object handlerWithNode(RelationNode node, String propertyName) {
			ABCNode abcNode = node.getAbcNode();
			return new ABCNodeProxy(abcNode).getElement(propertyName);
		};
		
	};
	
	/**
	 * 获得当前节点的信息对象
	 */
	static NodeSwitch<Byte> ENTITY_ELEMENT_GETTER = new NodeSwitch<Byte>() {
		public Object handlerWithNode(AttributeNode node, Byte b) {
			EntityAttrElement eElement = new EntityAttrElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getAbcattr());
			eElement.setDataType(node.getDatatype());
			eElement.setTagName("attribute");
			return eElement;
		};
		
		@Override
		public Object handlerWithNode(LabelNode node, Byte b) {
			EntityLabelElement eElement = new EntityLabelElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getAbcattr());
			eElement.setSubdomain(new HashSet<>(node.getSubdomains()));
			eElement.setTagName("label");
			return eElement;
		}
		
		@Override
		public Object handlerWithNode(MultiAttributeNode node, Byte b) {
			EntityMultiAttributeElement eElement = new EntityMultiAttributeElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getAbcattr());
			eElement.setTagName("multiattribute");
			return eElement;
		}
		
		public Object handlerWithNode(RelationNode node, Byte arg) {
			EntityRelationElement eElement = new EntityRelationElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getAbcNode().getAbcattr());
			eElement.setTagName("relation");
			eElement.setEntityName(node.getAbcNode().getTitle());
			eElement.setFullTitle(node.getFullTitle());
			eElement.setSubdomain(new HashSet<>(node.getLabelNode().getSubdomains()));
			return eElement;
		};
	};

	static NodeSwitch<Byte> ELEMENT_ENTITY_CREATOR = new NodeSwitch<Byte>() {
		@Override
		public Object handlerWithNode(MultiAttributeNode node, Byte arg) {
			SimpleEntity entity = new SimpleEntity(node.getTitle());
			MultiAttributeEntityProxy proxy = new MultiAttributeEntityProxy(entity);
			return proxy;
		}
		
		@Override
		public Object handlerWithNode(RelationNode node, Byte arg) {
			Entity entity = new Entity(node.getTitle());
			return new RelationEntityProxy(entity);
		}
	};

	
	
	

}
