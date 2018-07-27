package cn.sowell.datacenter.entityResolver.impl;

import org.springframework.util.Assert;

import com.abc.mapping.entity.Entity;

public class CommonEntityComponent implements EntityComponent {

	private final Entity principle;
	private final boolean toCreate;
	private boolean savedFile = false;
	
	public CommonEntityComponent(Entity principle, boolean toCreate) {
		super();
		Assert.notNull(principle);
		this.principle = principle;
		this.toCreate = toCreate;
	}

	
	@Override
	public Entity getEntity() {
		return principle;
	}

	@Override
	public boolean isToCreate() {
		return toCreate;
	}

	@Override
	public boolean isToUpdate() {
		return !toCreate;
	}

	@Override
	public String toJson() {
		return principle.toJson();
	}

	public void setSavedFile(boolean updatedFileProperty) {
		this.savedFile = updatedFileProperty;
	}


	@Override
	public boolean isSavedFile() {
		return this.savedFile;
	}

	
}
