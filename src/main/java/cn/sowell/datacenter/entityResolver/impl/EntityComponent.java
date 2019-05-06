package cn.sowell.datacenter.entityResolver.impl;

import com.abc.mapping.entity.RecordEntity;

public interface EntityComponent {
	RecordEntity getEntity();
	boolean isToCreate();
	boolean isToUpdate();
	boolean isSavedFile();
	boolean isCreatable();
	String toJson();
}
