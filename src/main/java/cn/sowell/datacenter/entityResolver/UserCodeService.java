package cn.sowell.datacenter.entityResolver;

import cn.sowell.copframe.utils.FormatUtils;

public interface UserCodeService {

	
	default String getUserCode(Object userPrinciple) {
		return FormatUtils.toString(userPrinciple);
	}

}
