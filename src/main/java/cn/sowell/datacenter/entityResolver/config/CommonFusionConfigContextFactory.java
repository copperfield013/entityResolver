package cn.sowell.datacenter.entityResolver.config;

import java.util.Set;
import java.util.function.Function;

import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.config.abst.Config;

public class CommonFusionConfigContextFactory extends AbstractFusionConfigContextFactory{

	Function<String, Set<FieldParserDescription>> fieldsGetter;
	
	protected CommonFusionConfigContextFactory(Config config) throws FusionConfigException {
		super(config);
	}

	@Override
	protected Set<FieldParserDescription> getFields(String module) {
		if(fieldsGetter != null) {
			return fieldsGetter.apply(module);
		}
		return null;
	}

	public void setFieldsGetter(Function<String, Set<FieldParserDescription>> fieldsGetter) {
		this.fieldsGetter = fieldsGetter;
	}
	

}
