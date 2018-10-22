package cn.sowell.datacenter.entityResolver.impl;

import com.abc.mapping.entity.Entity;

public interface EntityComponent {
	Entity getEntity();
	boolean isToCreate();
	boolean isToUpdate();
	boolean isSavedFile();
	boolean isCreatable();
	String toJson();
}
