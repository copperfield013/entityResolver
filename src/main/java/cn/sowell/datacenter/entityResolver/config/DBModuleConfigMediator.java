package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
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

	
	private DBModule getModuleOrThrowException(String moduleName) {
		Assert.hasText(moduleName, "传入的moduleName不能为空");
		DBModule module = cDao.getModule(moduleName);
		if(module != null) {
			return module;
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
		Assert.hasText(param.getMappingName(), "module对应的mappingName不能为空，必须指定");
		if(!TextUtils.hasText(param.getModuleName())) {
			param.setModuleName(uuid10());
			logger.debug("创建的模块名为空，生成随机模块名[" + param.getModuleName() + "]");
		}else {
			assertModuleNameExist(param.getModuleName(), false, "模块名[" + param.getModuleName() + "]已存在");
		}
		//assertMappingExists(param.getMappingName(), "创建的模块实体对应的配置[" + param.getMappingName() + "]不存在");
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

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void createModule(String moduleTitle, String mappingName) {
		createModule(new CreateModuleParam(moduleTitle, mappingName));
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void createModule(CreateModuleParam param) {
		validate(param);
		DBModule module = new DBModule();
		module.setTitle(param.getModuleTitle());
		module.setName(param.getModuleName());
		module.setMappingName(param.getMappingName());
		module.setCodeName(param.getCodeName());
		module.setTitleName(param.getTitleName());
		module.setDisabled(false);
		cDao.createModule(module);
	}

	

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void disableModule(String moduleName) {
		DBModule module = getModuleOrThrowException(moduleName);
		cDao.enableModule(module.getId(), false);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void enableModule(String moduleName) {
		DBModule module = getModuleOrThrowException(moduleName);
		cDao.enableModule(module.getId(), true);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void removeModule(String moduleName) {
		DBModule module = getModuleOrThrowException(moduleName);
		cDao.removeModule(module.getId());
	}

	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reassignMappingName(String moduleName, String mappingName, String codeName, String titleName) {
		Assert.hasText(mappingName, "传入的mappingName参数为空");
		DBModule module = getModuleOrThrowException(moduleName);
		if(!mappingName.equals(module.getMappingName())) {
			cDao.reassignMappingName(moduleName, mappingName, codeName, titleName);
		}
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reassignMappingName(String moduleName, String mappingName) {
		Assert.hasText(mappingName, "传入的mappingName参数为空");
		DBModule module = getModuleOrThrowException(moduleName);
		if(!mappingName.equals(module.getMappingName())) {
			cDao.reassignMappingName(moduleName, mappingName);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModulePropertyName(String moduleName, String codeName, String titleName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModulePropertyName(moduleName, codeName, titleName);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModuleCodeName(String moduleName, String codeName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModuleCodeName(moduleName, codeName);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModuleTitleName(String moduleName, String titleName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModuleTitleName(moduleName, titleName);
	}
	
}

