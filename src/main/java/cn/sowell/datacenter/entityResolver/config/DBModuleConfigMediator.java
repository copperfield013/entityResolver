package cn.sowell.datacenter.entityResolver.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.CreateModuleParam;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;


public class DBModuleConfigMediator implements ModuleConfigureMediator {

	SessionFactory sessionFactory;
	ModuleConfigDao cDao;
	
	Map<String, Module> moduleMap = null;
	
	Logger logger = Logger.getLogger(DBModuleConfigMediator.class);
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		ModuleConfigDaoImpl dao = new ModuleConfigDaoImpl();
		dao.setSessionFactory(sessionFactory);
		this.cDao = dao;
	}
	
	
	@Override
	public void refresh() {
		synchronized (this) {
			if(moduleMap != null) {
				synchronized (moduleMap) {
					logger.debug("执行模块数据刷新，将把moduleMap置为null");
					moduleMap = null;
				}
			}
		}
	}
	
	private Map<String, Module> getModuleMap(){
		synchronized (this) {
			if(moduleMap == null) {
				logger.debug("开始加载模块数据...");
				List<Module> modules = cDao.queryModules();
				modules.forEach(module->{
					putModuleMap(module.getName(), module);
				});
				if(moduleMap != null) {
					logger.debug(moduleMap.keySet());
				}
				logger.debug("模块数据记载完成，共加载了" + modules.size() + "个模块");
			}
		}
		return moduleMap == null? Maps.newHashMap(): moduleMap;
	}
	
	private void putModuleMap(String moduleName, Module module) {
		synchronized (this) {
			if(moduleMap == null) {
				moduleMap = new LinkedHashMap<>();
			}
		}
		synchronized (moduleMap) {
			if(module == null) {
				moduleMap.remove(moduleName);
			}else {
				moduleMap.put(moduleName, module);
			}
		}
	}
	
	private void reloadModuleToMap(String moduleName) {
		logger.debug("开始重新加载模块[" + moduleName + "]到缓存");
		putModuleMap(moduleName, cDao.getModule(moduleName));
		logger.debug("模块[" + moduleName + "]重新加载完成");
	}
	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Module getModule(String moduleName) {
		return getModuleMap().get(moduleName);
	}

	
	private DBModule getModuleOrThrowException(String moduleName) {
		Assert.hasText(moduleName, "传入的moduleName不能为空");
		DBModule module = (DBModule) getModule(moduleName);
		if(module != null) {
			return module;
		}else {
			throw new IllegalArgumentException("无法找到moduleName[" + moduleName + "]对应的模块");
		}
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Module> queryModules() {
		return new ArrayList<>(getModuleMap().values());
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Module> queryModules(QueryModuleCriteria criteria) {
		if(TextUtils.hasText(criteria.getModuleName())) {
			Module module = getModule(criteria.getModuleName());
			if(!criteria.isFilterDisabled() || !module.isDisabled()) {
				return Lists.newArrayList(module);
			}
		}else {
			return queryModules().stream()
				.filter(module->!criteria.isFilterDisabled() || !module.isDisabled())
				.collect(Collectors.toList())
				;
		}
		return Lists.newArrayList();
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
		Assert.notNull(param.getMappingId(), "module对应的mappingName不能为空，必须指定");
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
	public void createModule(String moduleTitle, Integer mappingId) {
		createModule(new CreateModuleParam(moduleTitle, mappingId));
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void createModule(CreateModuleParam param) {
		validate(param);
		DBModule module = new DBModule();
		module.setTitle(param.getModuleTitle());
		module.setName(param.getModuleName());
		module.setMappingId(param.getMappingId());
		module.setCodeName(param.getCodeName());
		module.setTitleName(param.getTitleName());
		module.setDisabled(false);
		cDao.createModule(module);
		reloadModuleToMap(module.getName());
	}

	

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void disableModule(String moduleName) {
		DBModule module = getModuleOrThrowException(moduleName);
		cDao.enableModule(module.getId(), false);
		reloadModuleToMap(module.getName());
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void enableModule(String moduleName) {
		DBModule module = getModuleOrThrowException(moduleName);
		cDao.enableModule(module.getId(), true);
		reloadModuleToMap(module.getName());
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void removeModule(String moduleName) {
		DBModule module = getModuleOrThrowException(moduleName);
		cDao.removeModule(module.getId());
		reloadModuleToMap(module.getName());
	}

	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reassignMappingName(String moduleName, Integer mappingId, String codeName, String titleName) {
		Assert.notNull(mappingId, "传入的mappingName参数为空");
		DBModule module = getModuleOrThrowException(moduleName);
		if(!mappingId.equals(module.getMappingId())) {
			cDao.reassignMappingId(moduleName, mappingId, codeName, titleName);
			reloadModuleToMap(module.getName());
		}
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reassignMappingName(String moduleName, Integer mappingId) {
		Assert.notNull(mappingId, "传入的mappingName参数为空");
		DBModule module = getModuleOrThrowException(moduleName);
		if(!mappingId.equals(module.getMappingId())) {
			cDao.reassignMappingId(moduleName, mappingId);
			reloadModuleToMap(module.getName());
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModule(String moduleName, String moduleTitle, String codeName, String titleName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModule(moduleName, moduleTitle, codeName, titleName);
		reloadModuleToMap(moduleName);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModulePropertyName(String moduleName, String codeName, String titleName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModulePropertyName(moduleName, codeName, titleName);
		reloadModuleToMap(moduleName);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModuleCodeName(String moduleName, String codeName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModuleCodeName(moduleName, codeName);
		reloadModuleToMap(moduleName);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateModuleTitleName(String moduleName, String titleName) {
		Assert.notNull(moduleName);
		getModuleOrThrowException(moduleName);
		cDao.updateModuleTitleName(moduleName, titleName);
		reloadModuleToMap(moduleName);
	}
	
}

