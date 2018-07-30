package cn.sowell.datacenter.entityResolver;

public interface UserCodeService {

	
	default String getUserCode(Object userPrinciple) {
		return null;
	}

}
