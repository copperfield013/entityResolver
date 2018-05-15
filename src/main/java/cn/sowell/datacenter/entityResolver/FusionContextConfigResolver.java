package cn.sowell.datacenter.entityResolver;

import java.util.Map;
import java.util.function.Consumer;

import com.abc.application.BizFusionContext;
import com.abc.mapping.entity.Entity;

public interface FusionContextConfigResolver {
	/**
	 * 根据实体属性的name-value创建实体对象，当属性没有配置时，<b>将会报错</b>
	 * @param map
	 * @return
	 */
	Entity createEntity(Map<String, Object> map);
	/**
	 * 根据实体属性的name-value创建实体对象，当属性没有配置时，<b>不会报错</b>
	 * @param map
	 * @return
	 */
	Entity createEntityIgnoreUnsupportedElement(Map<String, Object> map);
	
	/**
	 * 根据当前的配置，解析生成Entity后，直接利用当前配置保存Entity
	 * @param map 存放生成的Entity的数据的Map，map的key需要遵照对应配置文件的命名规则
	 * @param consumer 保存实体前，对上下文进行的操作
	 * @return 保存成功的entity的唯一编码
	 */
	String saveEntity(Map<String, Object> map, Consumer<BizFusionContext> consumer);
	
	/**
	 * 根据当前配置，解析生成Entity，并使用传入自定义的context， 保存Entity
	 * @param context
	 * @param map
	 * @return
	 */
	String saveEntity(BizFusionContext context, Map<String, Object> map);
	
	/**
	 * 创建实体的字段解析器对象
	 * @param entity
	 * @return
	 */
	ModuleEntityPropertyParser createParser(Entity entity);
	
	/**
	 * 
	 * @param fieldId
	 * @return
	 */
	FieldParserDescription getFieldParserDescription(Long fieldId);
	
	/**
	 * 根据字段的路径，获得对应的字段配置信息。
	 * 字段可以是一个multiattr或者relation等复合字段
	 * @param fieldPath
	 * @return
	 */
	FieldConfigure getFieldConfigure(String fieldPath);
	
	boolean hasLoadFieldDescription();
}
