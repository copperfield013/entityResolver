package cn.sowell.datacenter.entityResolver.config;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.sowell.datacenter.entityResolver.config.abst.Entity;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.AddEntityParam;
import cn.sowell.datacenter.entityResolver.config.param.CreateModuleParam;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

class RemoteModuleConfigureMediatorImpl extends UnicastRemoteObject implements RemoteModuleConfigureMediator{


	protected RemoteModuleConfigureMediatorImpl() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1563514479391808868L;
	ModuleConfigureMediator moduleConfigMediator;
	SyncModuleConfigStrategy syncStrategy = SyncModuleConfigStrategy.NONE;

	public void setModuleConfigMediator(ModuleConfigureMediator moduleConfigMediator) {
		this.moduleConfigMediator = moduleConfigMediator;
	}
	
	public void setSyncStrategy(SyncModuleConfigStrategy syncStrategy) {
		this.syncStrategy = syncStrategy;
	}
	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Module getModule(String moduleName) throws RemoteException{
		try {
			return moduleConfigMediator.getModule(moduleName);
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Module> queryModules() throws RemoteException {
		try {
			return moduleConfigMediator.queryModules();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Module> queryModules(QueryModuleCriteria criteria) throws RemoteException {
		try {
			return moduleConfigMediator.queryModules();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void createModule(String moduleTitle, String defMappingName) throws RemoteException {
		try {
			moduleConfigMediator.createModule(moduleTitle, defMappingName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void createModule(String moduleTitle, String defMappingName, String impTitle) throws RemoteException {
		try {
			moduleConfigMediator.createModule(moduleTitle, defMappingName, impTitle);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void createModule(CreateModuleParam param) throws RemoteException {
		try {
			moduleConfigMediator.createModule(param);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void disableModule(String moduleName) throws RemoteException {
		try {
			moduleConfigMediator.disableModule(moduleName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void enableModule(String moduleName) throws RemoteException {
		try {
			moduleConfigMediator.enableModule(moduleName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void removeModule(String moduleName) throws RemoteException {
		try {
			moduleConfigMediator.removeModule(moduleName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Entity getEntity(String entityId) throws RemoteException {
		try {
			return moduleConfigMediator.getEntity(entityId);
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void addModuleEntity(String moduleName, String mappingName) throws RemoteException {
		try {
			moduleConfigMediator.addModuleEntity(moduleName, mappingName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void addModuleEntity(AddEntityParam param) throws RemoteException {
		try {
			moduleConfigMediator.addModuleEntity(param);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void reassignMappingName(String entityId, String mappingName) throws RemoteException {
		try {
			moduleConfigMediator.reassignMappingName(entityId, mappingName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}
	
	@Override
	public void reassignMappingName(String entityId, String mappingName, String codeName, String titleName)
			throws RemoteException {
		try {
			moduleConfigMediator.reassignMappingName(entityId, mappingName, codeName, titleName);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void removeEntity(String entityId) throws RemoteException {
		try {
			moduleConfigMediator.removeEntity(entityId);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void switchDefaultEntity(String entityId) throws RemoteException {
		try {
			moduleConfigMediator.switchDefaultEntity(entityId);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void addModuleImportComposite(String entityId, String impTitle) throws RemoteException {
		try {
			moduleConfigMediator.addModuleImportComposite(entityId, impTitle);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void retitleModuleImport(String entityId, String impTitle) throws RemoteException {
		try {
			moduleConfigMediator.retitleModuleImport(entityId, impTitle);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void removeModuleImport(String entityId) throws RemoteException {
		try {
			moduleConfigMediator.removeEntity(entityId);
			syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

}
