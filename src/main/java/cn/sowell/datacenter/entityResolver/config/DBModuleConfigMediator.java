package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.abc.mapping.conf.MappingContainer;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Composite;
import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.AddEntityParam;
import cn.sowell.datacenter.entityResolver.config.param.CreateModuleParam;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;


public class DBModuleConfigMediator implements ModuleConfigureMediator {

	SessionFactory sessionFactory;
	ModuleConfigDao cDao;
	
	Logger logger = Logger.getLogger(DBModuleConfigMediator.class);
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		ModuleConfigDaoImpl dao = new ModuleConfigDaoImpl();
		dao.setSessionFactory(sessionFactory);
		this.cDao = dao;
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Module getModule(String moduleName) {
		return cDao.getModule(moduleName);
	}

	
	private Long getModuleIdOrThrowException(String moduleName) {
		Assert.hasText(moduleName, "传入的moduleName不能为空");
		Long moduleId = cDao.getModuleId(moduleName);
		if(moduleId != null) {
			return moduleId;
		}else {
			throw new IllegalArgumentException("无法找到moduleName[" + moduleName + "]对应的模块");
		}
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Module> queryModules() {
		return cDao.queryModules(new QueryModuleCriteria());
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Module> queryModules(QueryModuleCriteria criteria) {
		return cDao.queryModules(criteria);
	}
	
	private String uuid10() {
		return TextUtils.uuid(10, 62);
	}
	
	/**
	 * 检测创建模块时的参数
	 * @param param
	 */
	private void validate(CreateModuleParam param) {
		Assert.hasText(param.getModuleTitle(), "moduleTitle为模块标题，必须指定");
		
		if(!TextUtils.hasText(param.getModuleName())) {
			param.setModuleName(uuid10());
			logger.debug("创建的模块名为空，生成随机模块名[" + param.getModuleName() + "]");
		}else {
			assertModuleNameExist(param.getModuleName(), false, "模块名[" + param.getModuleName() + "]已存在");
		}
		if(param.isDefForImport() && !TextUtils.hasText(param.getImpTitle())) {
			param.setImpTitle(param.getModuleTitle());
		}
	}
	
	/**
	 * 
	 * @param param
	 * @return 返回moduleId
	 */
	private Long validate(AddEntityParam param) {
		Assert.hasText(param.getModuleName(), "创建模块实体时moduleName不能为空");
		Assert.hasText(param.getMappingName(), "创建模块实体时mappingName不能为空");
		assertMappingExists(param.getMappingName(), "创建的模块实体对应的配置[" + param.getMappingName() + "]不存在");
		Long moduleId = getModuleIdOrThrowException(param.getModuleName());
		if(!TextUtils.hasText(param.getEntityId())) {
			param.setEntityId(uuid10());
			logger.debug("创建模块的默认实体的id为空，生成随机id[" + param.getEntityId() + "]");
		}else {
			assertEntityExists(param.getEntityId(), false, "创建的实体[" + param.getEntityId() + "]已经存在");
		}
		if(param.isForImport() && !TextUtils.hasText(param.getImpTitle())) {
			param.setImpTitle("未命名条线");
		}
		return moduleId;
	}

	/**
	 * 判断实体是否已经存在
	 * @param entityId 实体id 
	 * @param isExists 是否存在
	 * @param message 如果实体存在状况不是isExists，那么抛出该信息的异常，否则返回对象
	 * @return
	 */
	private Entity assertEntityExists(String entityId, boolean isExists, String message) {
		Entity entity = getEntity(entityId);
		if((entity == null) == isExists) {
			throw new IllegalArgumentException(message);
		}
		return entity;
	}

	/**
	 * 判断模块名是否已经存在
	 * @param moduleName 模块名
	 * @param isExists 是否存在
	 * @param message 如果不是存在情况不是isExists，那么就的错误信息
	 */
	private void assertModuleNameExist(String moduleName, boolean isExists,  String message) {
		if((getModule(moduleName) == null) == isExists) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 判断mappingName对应的配置名是否可用
	 * @param defMappingName
	 * @param message
	 */
	private void assertMappingExists(String defMappingName, String message) {
		try {
			if(MappingContainer.getABCNode(defMappingName) == null) {
				throw new RuntimeException();
			}
		}catch(RuntimeException e) {
			throw new IllegalArgumentException(message, e);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void createModule(String moduleTitle, String defMappingName) {
		createModule(new CreateModuleParam(moduleTitle, defMappingName));
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void createModule(String moduleTitle, String defMappingName, String impTitle) {
		createModule(new CreateModuleParam(moduleTitle, defMappingName, impTitle));
	}


	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void createModule(CreateModuleParam param) {
		validate(param);
		TheModule module = new TheModule();
		module.setTitle(param.getModuleTitle());
		module.setName(param.getModuleName());
		cDao.createModule(module);
		
		AddEntityParam addEntityParam = new AddEntityParam(param.getModuleName(), param.getDefMappingName());
		addEntityParam.setCodeName(param.getDefCodeName());
		addEntityParam.setTitleName(param.getDefTitleName());
		addEntityParam.setEntityId(param.getDefEntityId());
		addEntityParam.setForImport(param.isDefForImport());
		addEntityParam.setImpTitle(param.getImpTitle());
		addModuleEntity(addEntityParam, true);
	}

	

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void disableModule(String moduleName) {
		Long moduleId = getModuleIdOrThrowException(moduleName);
		cDao.enableModule(moduleId, false);
	}

	


	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void enableModule(String moduleName) {
		Long moduleId = getModuleIdOrThrowException(moduleName);
		cDao.enableModule(moduleId, true);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void removeModule(String moduleName) {
		Long moduleId = getModuleIdOrThrowException(moduleName);
		cDao.removeModule(moduleId);
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Entity getEntity(String entityId) {
		return cDao.getEntity(entityId);
	}
	

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void addModuleEntity(String moduleName, String mappingName) {
		addModuleEntity(new AddEntityParam(moduleName, mappingName));
	}

	private void addModuleEntity(AddEntityParam param , boolean isDefault) {
		Long moduleId = validate(param);
		DBEntity entity = new DBEntity();
		entity.setCodeName(param.getCodeName());
		entity.setTitleName(param.getTitleName());
		entity.setId(param.getEntityId());
		entity.setDefault(isDefault);
		entity.setMappingName(param.getMappingName());
		entity.setModuleId(moduleId);
		cDao.addEntity(entity);
		if(param.isForImport()) {
			addModuleImportComposite(param.getEntityId(), param.getImpTitle());
		}
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void addModuleEntity(AddEntityParam param) {
		addModuleEntity(param, false);
	}

	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reassignMappingName(String entityId, String mappingName, String codeName, String titleName) {
		Entity entity = assertEntityExists(entityId, true, "实体[entityId=" + entityId + "]不存在");
		if(!entity.getMappingName().equals(mappingName)) {
			cDao.reassignMappingName(entityId, mappingName, codeName, titleName);
		}
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reassignMappingName(String entityId, String mappingName) {
		Entity entity = assertEntityExists(entityId, true, "实体[entityId=" + entityId + "]不存在");
		if(!entity.getMappingName().equals(mappingName)) {
			cDao.reassignMappingName(entityId, mappingName);
		}
	}


	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void removeEntity(String entityId) {
		Entity entity = assertEntityExists(entityId, true, "实体[entityId=" + entityId + "]不存在");
		if(entity.isDefault()) {
			throw new RuntimeException("无法移除模块默认实体[entityId=" + entityId + "]，请先重新指定其他实体为默认实体后移除");
		}
		int removed = cDao.removeEntity(entityId);
		if(removed != 1) {
			throw new RuntimeException("删除失败，以[entityId=" +  entityId + "]为条件删除了" + removed + "条记录");
		}
	}
	

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void switchDefaultEntity(String entityId) {
		Entity entity = assertEntityExists(entityId, true, "没有找到实体[entityId=" + entityId + "]");
		if(!entity.isDefault()) {
			Entity defEntity = cDao.getDefaultEntityOfEntitySiblings(entityId);
			cDao.changeEntityDefaultStatus(defEntity.getId(), false);
			cDao.changeEntityDefaultStatus(entityId, true);
		}
	}
	

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void addModuleImportComposite(String entityId, String impTitle) {
		Assert.hasText(impTitle, "添加的导入条线的标题必须指定");
		assertEntityExists(entityId, true, "添加的导入条线对应的实体[entityId=" + entityId + "]不存在");
		assertImportCompositeUnexists(entityId, "导入条线对应的实体[entityId=" + entityId + "]已经绑定了其他导入条线，不能再次绑定");
		TheComposite composite = new TheComposite();
		composite.setName(uuid10());
		composite.setTitle(impTitle);
		composite.setEntityId(entityId);
		cDao.addImportComposite(composite);
	}

	private void assertImportCompositeUnexists(String entityId, String message) {
		Composite composite = cDao.getImportComposite(entityId);
		if(composite != null) {
			throw new IllegalArgumentException(message);
		}
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void retitleModuleImport(String entityId, String impTitle) {
		int retitled = cDao.retitleImportComposite(entityId, impTitle);
		if(retitled != 1) {
			throw new RuntimeException("操作失败，根据[entityId=" + entityId + "]匹配到" + retitled + "个导入条线");
		}
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void removeModuleImport(String entityId) {
		int removed = cDao.removeImportComposite(entityId);
		if(removed != 1) {
			throw new RuntimeException("操作失败，根据[entityId=" + entityId + "]匹配到" + removed + "个导入条线");
		}
	}

}

