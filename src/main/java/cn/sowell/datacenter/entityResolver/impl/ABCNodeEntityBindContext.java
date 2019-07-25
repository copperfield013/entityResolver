package cn.sowell.datacenter.entityResolver.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.meta.struc.er.Struc;
import cn.sowell.datacenter.entityResolver.EntityBindContext;
import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.EntityProxy;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.PropertyNamePartitions;
import cn.sowell.datacenter.entityResolver.exception.UnsupportedEntityElementException;

public class ABCNodeEntityBindContext extends AbstractEntityBindContext {
	private ABCNodeProxy node;
	
	protected Map<String, FieldParserDescription> fieldMap;
	
	
	Logger logger = Logger.getLogger(ABCNodeEntityBindContext.class);
	
	public ABCNodeEntityBindContext(Struc rootNode, RecordEntity entity) {
		this(new ABCNodeProxy(rootNode), new ABCEntityProxy(entity));
	}

	public ABCNodeEntityBindContext(ABCNodeProxy thisNode, EntityProxy thisEntity) {
		super(thisEntity);
		this.node = thisNode;
	}

	
	@Override
	public EntityBindContext getElementAutoCreate(PropertyNamePartitions propName) {
		return getElement(propName, true);
	}
	
	@Override
	public EntityBindContext getElementIfExists(PropertyNamePartitions propName) {
		return getElement(propName, false);
	}
	
	private EntityBindContext getElement(PropertyNamePartitions propName, boolean autoCreate) {
		Assert.isInstanceOf(EntitiesContainedEntityProxy.class, this.entity);
		//获得子节点的信息对象
		ABCNodeProxy eleNode = this.node.getElement(propName.getMainPartition());
		if(eleNode != null) {
			try {
				//根据属性名获得子节点对象
				EntityProxy eleEntity = getElementEntityProxy(propName);
				if(eleEntity == null) {
					if(autoCreate) {
						//无法获得子节点时，创建一个子节点
						eleEntity = eleNode.createElementEntity();
						//将子节点对象放到当前节点中
						((EntitiesContainedEntityProxy) this.entity).putEntity(propName, eleEntity);
					}else {
						return null;
					}
				}
				return new ABCNodeEntityBindContext(eleNode, eleEntity);
			} catch (Exception e) {
				throw new RuntimeException("获得子节点时发生错误", e);
			}
		}else {
			logger.error("没有找到节点" + propName.getMainPartition());
			return null;
		}
	}
	
	
	private EntityProxy getElementEntityProxy(PropertyNamePartitions namePartitions) {
		List<EntityProxy> entities = ((EntitiesContainedEntityProxy) this.entity).getEntityElements(namePartitions.getMainPartition());
		if(entities != null) {
			int index = namePartitions.getIndex();
			if(index < entities.size()) {
				return entities.get(index);
			}
		}
		return null;
	}

	@Override
	protected EntityElement getEntityElement(String propName) {
		ABCNodeProxy ele = this.node.getElement(propName);
		if(ele != null) {
			return ele.getEntityElement();
		}else {
			throw new UnsupportedEntityElementException("没有找到属性[" + propName + "]");
		}
	}
	
	public ABCNodeProxy getAbcNode() {
		return this.node;
	}
	
	
	
}
