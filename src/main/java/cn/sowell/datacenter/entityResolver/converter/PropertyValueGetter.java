package cn.sowell.datacenter.entityResolver.converter;

import cn.sowell.datacenter.entityResolver.FieldParserDescription;

/**
 * 用于parser中将entity中的字段值转换成本地可用的对象
 * @author Copperfield
 * @date 2018年7月24日 下午5:31:33
 */
public interface PropertyValueGetter {

	boolean support(FieldParserDescription field, Object propertyGetterArgument);

	Object invoke(PropertyValueGetContext context);

}
