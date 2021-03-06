package cn.sowell.datacenter.entityResolver.config;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cn.sowell.copframe.dao.deferedQuery.DeferedParamQuery;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

public class ModuleConfigDaoImpl implements ModuleConfigDao{

	SessionFactory sessionFactory;
	
	DatabaseQuery dbQuery = DatabaseQuery.getInstance();
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public DBModule getModule(String moduleName) {
		QueryModuleCriteria criteria = new QueryModuleCriteria();
		criteria.setModuleName(moduleName);
		List<Module> modules = queryModules(criteria);
		if(modules.size() > 0) {
			return (DBModule) modules.get(0);
		}else if(modules.size() == 0){
			return null;
		}else {
			throw new RuntimeException("通过[moduleName=" + moduleName + "]找到[" + modules.size() + "]条记录，不符合规范");
		}
	}

	
	@Override
	public List<Module> queryModules() {
		Session session = sessionFactory.getCurrentSession();
		DeferedParamQuery dQuery = dbQuery.getModuleQuery();
		return dbQuery.queryModule(dQuery.createSQLQuery(session, false, null), session);
	}
	
	@Override
	public List<Module> queryModules(QueryModuleCriteria criteria) {
		Session session = sessionFactory.getCurrentSession();
		DeferedParamQuery dQuery = dbQuery.getModuleQuery(criteria);
		return dbQuery.queryModule(dQuery.createSQLQuery(session, false, null), session);
	}

	@Override
	public Long createModule(DBModule module) {
		DeferedParamQuery dQuery = dbQuery.getCreateModuleQuery(module);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
			.executeUpdate();
		return getModuleId(module.getName());
	}


	@Override
	public Long getModuleId(String moduleName) {
		DeferedParamQuery dQuery = dbQuery.getQueryModuleIdQuery(moduleName);
		return FormatUtils.toLong(dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.uniqueResult());
	}

	@Override
	public void enableModule(Long moduleId, boolean toEnable) {
		DeferedParamQuery dQuery = dbQuery.getEnableModuleQuery(moduleId, toEnable);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}

	@Override
	public void removeModule(Long moduleId) {
		DeferedParamQuery dQuery = dbQuery.getRemoveModuleQuery(moduleId);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}


	@Override
	public void reassignMappingId(String moduleName, Integer mappingId) {
		DeferedParamQuery dQuery = dbQuery.getReassignModuleMappingIdQuery(moduleName, mappingId);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}
	
	@Override
	public void reassignMappingId(String entityId, Integer mappingId, String codeName, String titleName) {
		DeferedParamQuery dQuery = dbQuery.getReassignModuleMappingIdQuery(entityId, mappingId, codeName, titleName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}

	@Override
	public void updateModule(String moduleName, String moduleTitle, String codeName, String titleName) {
		DeferedParamQuery dQuery = dbQuery.getUpdateModuleQuery(moduleName, moduleTitle, codeName, titleName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}
	
	@Override
	public void updateModulePropertyName(String moduleName, String codeName, String titleName) {
		DeferedParamQuery dQuery = dbQuery.getUpdateModulePropertyNameQuery(moduleName, codeName, titleName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}
	
	@Override
	public void updateModuleCodeName(String moduleName, String codeName) {
		DeferedParamQuery dQuery = dbQuery.getUpdateModuleCodeNameQuery(moduleName, codeName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}
	
	@Override
	public void updateModuleTitleName(String moduleName, String titleName) {
		DeferedParamQuery dQuery = dbQuery.getUpdateModuleTitleNameQuery(moduleName, titleName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}
	
}
