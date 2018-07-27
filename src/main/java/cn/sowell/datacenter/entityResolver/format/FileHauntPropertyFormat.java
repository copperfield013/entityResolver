package cn.sowell.datacenter.entityResolver.format;

import cn.sowell.copframe.spring.file.FileHaunt;
import cn.sowell.copframe.spring.file.FilePublisher;

public class FileHauntPropertyFormat implements PropertyFormat {

	@Override
	public String format(Object obj) {
		if(obj instanceof FileHaunt) {
			FileHaunt file = (FileHaunt) obj;
			FilePublisher publisher = FilePublisher.getContextInstance();
			return publisher.publish(file);
		}
		return null;
	}

}
