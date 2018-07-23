package cn.sowell.datacenter.entityResolver;

public interface UserCodeService {

	default String getCurrentUserCode() {
		return null;
	}

}
