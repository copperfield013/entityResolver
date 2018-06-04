package cn.sowell.datacenter.entityResolver.config;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import cn.sowell.copframe.utils.rmi.StaticRmiSocketFactory;

public class RmiModuleConfigureMediatorServer implements InitializingBean{
	

	ModuleConfigureMediator moduleConfigMediator;
	DBFusionConfigContextFactory dBFusionConfigContextFactory;
	static RemoteModuleConfigureMediator rMediator;
	private String host = "0.0.0.0";
	private Integer port = 32336;
	private String serverName = "moduleConfigMediator";
	
	Logger logger = Logger.getLogger(RmiModuleConfigureMediatorServer.class);
	private static Registry registry;
	
	
	
	public void setModuleConfigMediator(ModuleConfigureMediator moduleConfigMediator) {
		this.moduleConfigMediator = moduleConfigMediator;
	}
	
	public void setdBFusionConfigContextFactory(DBFusionConfigContextFactory dBFusionConfigContextFactory) {
		this.dBFusionConfigContextFactory = dBFusionConfigContextFactory;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		getMediatorInstance();
	}
	
	private synchronized RemoteModuleConfigureMediator getMediatorInstance() throws MalformedURLException, RemoteException, AlreadyBoundException {
		if(rMediator == null) {
			synchronized (RmiModuleConfigureMediatorServer.class) {
				RemoteModuleConfigureMediatorImpl mediator = new RemoteModuleConfigureMediatorImpl();
				if(dBFusionConfigContextFactory != null) {
					mediator.setSyncStrategy(new SyncModuleConfigStrategy() {
						
						@Override
						public void sync() {
							dBFusionConfigContextFactory.sync();
						}
					});
				}
				mediator.setModuleConfigMediator(moduleConfigMediator);
				//将中介对象绑定到rmi地址上
				StaticRmiSocketFactory regFac = new StaticRmiSocketFactory(host, port);
				registry = LocateRegistry.createRegistry(port, regFac, regFac);
				registry.rebind(serverName, mediator);
				rMediator = mediator;
			}
		}
		return rMediator;
	}
	
	public void stopServer() {
		synchronized (rMediator) {
			try {
				registry.unbind(serverName);
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public RemoteModuleConfigureMediator getMediator() {
		try {
			return getMediatorInstance();
		} catch (MalformedURLException | RemoteException | AlreadyBoundException e) {
			logger.debug("无法获得远程服务对象", e);
			return null;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	

	

	
}
