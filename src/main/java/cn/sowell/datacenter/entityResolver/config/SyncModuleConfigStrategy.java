package cn.sowell.datacenter.entityResolver.config;

public interface SyncModuleConfigStrategy {

	SyncModuleConfigStrategy NONE = new SyncModuleConfigStrategy() {
		@Override
		public void sync() {
		}
	};

	void sync();

}
