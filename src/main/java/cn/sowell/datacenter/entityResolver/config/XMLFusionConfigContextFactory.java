package cn.sowell.datacenter.entityResolver.config;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;

import cn.sowell.copframe.utils.xml.Dom4jNode;
import cn.sowell.copframe.utils.xml.XMLException;
import cn.sowell.copframe.utils.xml.XmlNode;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FieldService;

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
		Config config = new Config();
		config.setModules(new LinkedHashSet<Module>());
		List<XmlNode> eleModules = xml.getElements("module");
		for (XmlNode eleModule : eleModules) {
			Module module = new Module();
			module.setName(eleModule.getAttribute("name"));
			module.setTitle(eleModule.getAttribute("title"));
			
			handleEntities(module, eleModule);
			handleImports(module, eleModule);
			handleFunction(module, eleModule);
			
			
			config.getModules().add(module);
			
		}
		return config;
	}

	private static void handleFunction(Module module, XmlNode eleModule) {
		XmlNode eFunctions = eleModule.getFirstElement("functions");
		if(eFunctions != null) {
			module.setFunctions(new Functions());
			List<XmlNode> eleFunctions = eFunctions.getElements("function");
			for (XmlNode eFunction : eleFunctions) {
				Function function = new Function();
				function.setName(eFunction.getAttribute("name"));
				module.getFunctions().getFunctions().add(function);
			}
		}
	}

	private static void handleImports(Module module, XmlNode eleModule) {
		XmlNode eImport = eleModule.getFirstElement("import");
		if(eImport != null) {
			module.setImport(new Import());
			List<XmlNode> eleComposites = eImport.getElements("composite");
			for (XmlNode eComposite : eleComposites) {
				Composite composite = new Composite();
				composite.setName(eComposite.getAttribute("name"));
				composite.setTitle(eComposite.getAttribute("title"));
				composite.setEntityId(eComposite.getAttribute("entity-id"));
				module.getImport().getComposites().add(composite);
			}
		}
	}

	private static void handleEntities(Module module, XmlNode eleModule) {
		module.setEntities(new LinkedHashSet<Entity>());
		List<XmlNode> eleEntities = eleModule.getElements("entity");
		for (XmlNode eleEntity : eleEntities) {
			Entity entity = new Entity();
			entity.setId(eleEntity.getAttribute("id"));
			entity.setMappingName(eleEntity.getAttribute("mapping-name"));
			entity.setDefault("true".equals(eleEntity.getAttribute("default")));
			XmlNode codeEle = eleEntity.getFirstElement("code"),
					titleEle = eleEntity.getFirstElement("title");
			if(codeEle != null) {
				entity.setCodeName(codeEle.getAttribute("name"));
			}
			if(titleEle != null) {
				entity.setTitleName(titleEle.getAttribute("name"));
			}
			module.getEntities().add(entity);
		}
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
