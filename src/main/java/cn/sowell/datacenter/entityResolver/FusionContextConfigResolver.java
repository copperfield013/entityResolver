package cn.sowell.datacenter.entityResolver;

import java.util.Map;
import java.util.function.Consumer;

import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.hc.HCFusionContext;
import cho.carbon.meta.criteria.model.ModelConJunction;
import cn.sowell.datacenter.entityResolver.impl.EntityComponent;
import cn.sowell.datacenter.entityResolver.impl.RabcModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelSelectionEntityPropertyParser;

public interface FusionContextConfigResolver {
	/**
	 * 根据实体属性的name-value创建实体对象，当属性没有配置时，<b>将会报错</b>
	 * @param map
	 * @return
	 */
	EntityComponent createEntity(Map<String, Object> map);
	/**
	 * 根据实体属性的name-value创建实体对象，当属性没有配置时，<b>不会报错</b>
	 * @param map
	 * @return
	 */
	EntityComponent createEntityIgnoreUnsupportedElement(Map<String, Object> map);
	
	/**
	 * 根据当前的配置，解析生成Entity后，直接利用当前配置保存Entity
	 * @param map 存放生成的Entity的数据的Map，map的key需要遵照对应配置文件的命名规则
	 * @param consumer 保存实体前，对上下文进行的操作
	 * @return 保存成功的entity的唯一编码
	 */
	String saveEntity(Map<String, Object> map, Consumer<HCFusionContext> consumer, Object user);
	
	String saveEntity(Map<String, Object> entityMap, Consumer<HCFusionContext> consumer, Object user,
			Map<Integer, ModelConJunction> criteriasMap);
	
	/**
	 * 根据当前配置，解析生成Entity，并使用传入自定义的context， 保存Entity
	 * @param context
	 * @param map
	 * @return
	 */
	String saveEntity(HCFusionContext context, Map<String, Object> map,
			Map<Integer, ModelConJunction> criteriasMap);
	
	
	
	/**
	 * 创建实体的字段解析器对象
	 * @param entity
	 * @return
	 */
	ModuleEntityPropertyParser createParser(RecordEntity entity, Object user, Object propertyGetterArgument);
	
	RelSelectionEntityPropertyParser createRelationParser(RecordEntity entity, String relationName, Object user);
	
	RabcModuleEntityPropertyParser createRabcEntityParser(RecordEntity entity, Object user,
			Object propertyGetterArgument);
	
	/**
	 * 
	 * @param fieldId
	 * @return
	 */
	FieldParserDescription getFieldParserDescription(Integer fieldId);
	
	/**
	 * 根据字段的路径，获得对应的字段配置信息。
	 * 字段可以是一个multiattr或者relation等复合字段
	 * @param fieldPath
	 * @return
	 */
	FieldConfigure getFieldConfigure(String fieldPath);
	
	boolean hasLoadFieldDescription();
	
	boolean isEntityWritable();
	
}
