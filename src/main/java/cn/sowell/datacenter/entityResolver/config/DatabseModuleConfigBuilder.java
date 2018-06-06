package cn.sowell.datacenter.entityResolver.config;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cn.sowell.datacenter.entityResolver.config.abst.Config;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

public class DatabseModuleConfigBuilder implements ModuleConfigBuilder{

	private final DatabaseQuery databaseQuery;
	private final SessionFactory sessionFactory;

	public DatabseModuleConfigBuilder(DatabaseQuery databaseConfig, SessionFactory sessionFactory) {
		super();
		this.databaseQuery = databaseConfig;
		this.sessionFactory = sessionFactory;
	}
	
	
		
	@Override
	public Config getConfig() {
		Session session = sessionFactory.getCurrentSession();
		SQLQuery configQuery = databaseQuery.getModuleQuery(new QueryModuleCriteria()).createSQLQuery(session, false, null);
		TheConfig config = new TheConfig();
		config.setModules(databaseQuery.queryModule(configQuery, session));
		return config;
		
	}

}
