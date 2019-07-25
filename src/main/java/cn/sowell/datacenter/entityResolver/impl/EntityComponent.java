package cn.sowell.datacenter.entityResolver.impl;

import cho.carbon.entity.entity.RecordEntity;

public interface EntityComponent {
	RecordEntity getEntity();
	boolean isToCreate();
	boolean isToUpdate();
	boolean isSavedFile();
	boolean isCreatable();
	String toJson();
}
