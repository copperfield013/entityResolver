package cn.sowell.datacenter.entityResolver.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;

import cn.sowell.copframe.utils.xml.Dom4jNode;
import cn.sowell.copframe.utils.xml.XMLException;
import cn.sowell.copframe.utils.xml.XmlNode;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.config.abst.Config;
import cn.sowell.datacenter.entityResolver.config.abst.Module;

public class XMLFusionConfigContextFactory extends AbstractFusionConfigContextFactory{

	FieldService fieldService;
	
	public XMLFusionConfigContextFactory(XmlNode xml)
			throws FusionConfigException {
		super(toConfig(xml));
	}
	
	public XMLFusionConfigContextFactory(Resource resouce) throws FusionConfigException, IOException, XMLException {
		this(new Dom4jNode(resouce.getInputStream()));
	}


	private static Config toConfig(XmlNode xml) {
		TheConfig config = new TheConfig();
		config.setModules(new ArrayList<Module>());
		List<XmlNode> eleModules = xml.getElements("module");
		for (XmlNode eleModule : eleModules) {
			TheModule module = new TheModule();
			module.setName(eleModule.getAttribute("name"));
			module.setTitle(eleModule.getAttribute("title"));
			module.setMappingName(eleModule.getAttribute("mapping-name"));
			XmlNode codeEle = eleModule.getFirstElement("code"),
					titleEle = eleModule.getFirstElement("title");
			if(codeEle != null) {
				module.setCodeName(codeEle.getAttribute("name"));
			}
			if(titleEle != null) {
				module.setTitleName(titleEle.getAttribute("name"));
			}
			if("disabled".equals(eleModule.getAttribute("disabled"))) {
				module.setDisabled(true);
			}
			config.getModules().add(module);
		}
		return config;
	}

	@Override
	protected Set<FieldParserDescription> getFields(String module) {
		if(fieldService != null) {
			return fieldService.getFieldDescriptions(module);
		}else {
			return null;
		}
	}

	public void setFieldService(FieldService fieldService) {
		this.fieldService = fieldService;
	}

}
