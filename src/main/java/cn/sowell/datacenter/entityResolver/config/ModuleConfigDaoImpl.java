package cn.sowell.datacenter.entityResolver.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cn.sowell.copframe.dao.deferedQuery.DeferedParamQuery;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Composite;
import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

public class ModuleConfigDaoImpl implements ModuleConfigDao{

	SessionFactory sessionFactory;
	
	DatabaseQuery dbQuery = DatabaseQuery.getInstance();
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public Module getModule(String moduleName) {
		QueryModuleCriteria criteria = new QueryModuleCriteria();
		criteria.setModuleName(moduleName);
		List<Module> modules = queryModules(criteria);
		if(modules.size() > 0) {
			return modules.get(0);
		}
		return null;
	}

	@Override
	public List<Module> queryModules(QueryModuleCriteria criteria) {
		Session session = sessionFactory.getCurrentSession();
		DeferedParamQuery dQuery = dbQuery.getModuleQuery(criteria);
		Set<Module> modules = dbQuery.queryModule(dQuery.createSQLQuery(session, false, null), session);
		return new ArrayList<>(modules);
	}

	@Override
	public Long createModule(TheModule module) {
		DeferedParamQuery dQuery = dbQuery.getCreateModuleQuery(module);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
			.executeUpdate();
		return getModuleId(module.getName());
	}

	@Override
	public Long addEntity(DBEntity entity) {
		DeferedParamQuery dQuery = dbQuery.getCreateEntityQuery(entity);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
			.executeUpdate();
		return getEntityId(entity.getId());
			
	}

	@Override
	public Long getEntityId(String entityId) {
		DeferedParamQuery dQuery = dbQuery.getQueryEntityIdQuery(entityId);
		return FormatUtils.toLong(dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.uniqueResult());
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
	public Entity getEntity(String entityId) {
		SQLQuery query = dbQuery.getQueryEntityQuery(entityId, sessionFactory.getCurrentSession());
		return (Entity) query.uniqueResult();
	}

	@Override
	public void reassignMappingName(String entityId, String mappingName) {
		DeferedParamQuery dQuery = dbQuery.getReassignMappingNameQuery(entityId, mappingName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}
	
	@Override
	public void reassignMappingName(String entityId, String mappingName, String codeName, String titleName) {
		DeferedParamQuery dQuery = dbQuery.getReassignMappingNameQuery(entityId, mappingName, codeName, titleName);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}

	@Override
	public int removeEntity(String entityId) {
		DeferedParamQuery dQuery = dbQuery.getRemoveEntityQuery(entityId);
		return dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}

	@Override
	public Entity getDefaultEntityOfEntitySiblings(String entityId) {
		DeferedParamQuery dQuery = dbQuery.getDefaultEntityOfEntitySiblingsQuery(entityId);
		return (Entity) dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.uniqueResult();
	}

	@Override
	public void changeEntityDefaultStatus(String entityId, boolean asDefault) {
		DeferedParamQuery dQuery = dbQuery.getChangeEntityDefaultStatusQuery(entityId, asDefault);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}

	@Override
	public Composite getImportComposite(String entityId) {
		DeferedParamQuery dQuery = dbQuery.getImportCompositeQuery(entityId);
		return (Composite) dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.uniqueResult();
	}

	@Override
	public void addImportComposite(TheComposite composite) {
		DeferedParamQuery dQuery = dbQuery.getAddImportCompositeQuery(composite);
		dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
				.executeUpdate();
	}

	@Override
	public int retitleImportComposite(String entityId, String impTitle) {
		DeferedParamQuery dQuery = dbQuery.getRetitleImportCompositeQuery(entityId, impTitle);
		return dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
			.executeUpdate();
	}

	@Override
	public int removeImportComposite(String entityId) {
		DeferedParamQuery dQuery = dbQuery.getRemoveImportCompositeQuery(entityId);
		return dQuery.createSQLQuery(sessionFactory.getCurrentSession(), false, null)
			.executeUpdate();
	}
	
}
