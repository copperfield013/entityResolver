package cn.sowell.datacenter.entityResolver.config;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiModuleConfigureMediatorClient {
	
	private String host;
	private Integer port;
	private String serverName;

	public RemoteModuleConfigureMediator getMediator() throws RemoteException, NotBoundException, MalformedURLException {
		//StaticRmiSocketFactory regFac = new StaticRmiSocketFactory(host, port);
		//Registry reg = LocateRegistry.createRegistry(port, regFac, regFac);
		String url = "rmi://" + host + ":" + port + "/" + serverName;
		System.setSecurityManager (new SecurityManager() {
			public void checkConnect (String host, int port) {}
			public void checkConnect (String host, int port, Object context) {}
		});
		return (RemoteModuleConfigureMediator) Naming.lookup(url);
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
