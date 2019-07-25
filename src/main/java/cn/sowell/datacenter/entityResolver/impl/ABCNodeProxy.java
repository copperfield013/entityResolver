package cn.sowell.datacenter.entityResolver.impl;

import java.util.HashSet;

import cho.carbon.entity.entity.Entity;
import cho.carbon.entity.entity.LeafEntity;
import cho.carbon.meta.constant.ModelItemInfo;
import cho.carbon.meta.struc.er.Field;
import cho.carbon.meta.struc.er.Group2D;
import cho.carbon.meta.struc.er.RStruc;
import cho.carbon.meta.struc.er.Struc;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.EntityProxy;

public class ABCNodeProxy {
	public static final String CODE_NODE_NAME = ModelItemInfo.PCOL_RECORDCODE;
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
			throw new RuntimeException("没有唯一编码字段");
			/*
			 * if(node instanceof ABCNode || node instanceof MultiAttributeNode || node
			 * instanceof RelationNode) { IAttributeNode codeNode = new AttributeNodeImpl();
			 * codeNode.setName(CODE_PROPERTY_NAME_NORMAL);
			 * codeNode.setAbcattr(CODE_NODE_NAME);
			 * codeNode.setDatatype(AttributeValueType.STRING); return new
			 * ABCNodeProxy(codeNode); }
			 */
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
			if(node instanceof RStruc) {
				result = handlerWithNode((RStruc)node, arg);
			}else if(node instanceof Struc) {
				result = handlerWithNode((Struc)node, arg);
			}else if(node instanceof Group2D) {
				result = handlerWithNode((Group2D)node, arg);
			}else if(node instanceof Field) {
				result = handlerWithNode((Field)node, arg);
			}else {
				result = handlerWithOther(node, arg);
			}
			Object globalResult = handlerGlobal(node, arg);
			return result == null? globalResult: result;
		}
		public default Object handlerWithOther(Object node, T arg) { throw new RuntimeException("无法识别的标签对象");}
		public default Object handlerGlobal(Object node, T arg) {return null;}
		public default Object handlerWithNode(Struc node, T arg) {return null;};
		public default Object handlerWithNode(Group2D node, T arg) {return null;};
		public default Object handlerWithNode(RStruc node, T arg) {return null;};
		public default Object handlerWithNode(Field node, T arg) {return null;};
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
		public Object handlerWithNode(Struc node, String propertyName) {
			return FormatUtils.coalesce(
					node.findField(propertyName),
					node.findGroup2D(propertyName),
					node.findRStruc(propertyName)
					);
		}
		public Object handlerWithNode(Group2D node, String propertyName) {
			return node.findField(propertyName);
		}
		public Object handlerWithNode(RStruc node, String propertyName) {
			Struc abcNode = node.getPointStruc();
			return new ABCNodeProxy(abcNode).getElement(propertyName);
		};
		
	};
	
	/**
	 * 获得当前节点的信息对象
	 */
	static NodeSwitch<Byte> ENTITY_ELEMENT_GETTER = new NodeSwitch<Byte>() {
		public Object handlerWithNode(Field node, Byte b) {
			EntityAttrElement eElement = new EntityAttrElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getItemCode());
			eElement.setDataType(node.getValueType());
			eElement.setTagName("attribute");
			return eElement;
		};
		
		@Override
		public Object handlerWithNode(Group2D node, Byte b) {
			EntityMultiAttributeElement eElement = new EntityMultiAttributeElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getItemCode());
			eElement.setTagName("multiattribute");
			return eElement;
		}
		
		public Object handlerWithNode(RStruc node, Byte arg) {
			EntityRelationElement eElement = new EntityRelationElement();
			eElement.setName(node.getTitle());
			eElement.setAbcattr(node.getItemCode());
			eElement.setTagName("relation");
			eElement.setEntityName(node.getTitle());
			eElement.setFullTitle(node.getTitlePath());
			eElement.setSubdomain(new HashSet<>(node.getRelationTypeNames()));
			return eElement;
		};
	};

	static NodeSwitch<Byte> ELEMENT_ENTITY_CREATOR = new NodeSwitch<Byte>() {
		@Override
		public Object handlerWithNode(Group2D node, Byte arg) {
			LeafEntity entity = new LeafEntity(node.getTitle());
			MultiAttributeEntityProxy proxy = new MultiAttributeEntityProxy(entity);
			return proxy;
		}
		
		@Override
		public Object handlerWithNode(RStruc node, Byte arg) {
			Entity entity = new Entity(node.getTitle());
			return new RelationEntityProxy(entity);
		}
	};

	
	
	

}
