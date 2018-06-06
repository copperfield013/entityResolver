package cn.sowell.datacenter.entityResolver.config;

import java.util.Set;

import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.config.abst.Config;

public class CommonFusionConfigContextFactory extends AbstractFusionConfigContextFactory{

	FieldService fieldService;
	
	protected CommonFusionConfigContextFactory(Config config) throws FusionConfigException {
		super(config);
	}

	@Override
	protected Set<FieldParserDescription> getFields(String module) {
		if(fieldService != null) {
			return this.fieldService.getFieldDescriptions(module);
		}
		return null;
	}

	public void setFieldsService(FieldService fieldService) {
		this.fieldService = fieldService;
	}
	

}
