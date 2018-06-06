package cn.sowell.datacenter.entityResolver.config;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.sowell.datacenter.entityResolver.config.abst.Module;
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
	public Module getModule(String moduleName) throws RemoteException {
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
			return moduleConfigMediator.queryModules(criteria);
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void createModule(String moduleTitle, String mappingName) throws RemoteException {
		try {
			moduleConfigMediator.createModule(moduleTitle, mappingName);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void createModule(CreateModuleParam param) throws RemoteException {
		try {
			moduleConfigMediator.createModule(param);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void disableModule(String moduleName) throws RemoteException {
		try {
			moduleConfigMediator.disableModule(moduleName);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void enableModule(String moduleName) throws RemoteException {
		try {
			moduleConfigMediator.enableModule(moduleName);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void removeModule(String moduleName) throws RemoteException {
		try {
			moduleConfigMediator.removeModule(moduleName);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void reassignMappingName(String moduleName, String mappingName) throws RemoteException {
		try {
			moduleConfigMediator.reassignMappingName(moduleName, mappingName);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void reassignMappingName(String moduleName, String mappingName, String codeName, String titleName)
			throws RemoteException {
		try {
			moduleConfigMediator.reassignMappingName(moduleName, mappingName, codeName, titleName);
			this.syncStrategy.sync();
		}catch (Exception e) {
			throw new RemoteException("", e);
		}
	}
	
	

}
