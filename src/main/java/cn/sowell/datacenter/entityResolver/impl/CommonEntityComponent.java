package cn.sowell.datacenter.entityResolver.impl;

import org.springframework.util.Assert;

import cho.carbon.entity.entity.RecordEntity;

public class CommonEntityComponent implements EntityComponent {

	private final RecordEntity principle;
	private final boolean toCreate;
	private boolean savedFile = false;
	private final boolean hasTitle;
	
	public CommonEntityComponent(RecordEntity principle, boolean toCreate, boolean hasTitle) {
		super();
		Assert.notNull(principle);
		this.principle = principle;
		this.toCreate = toCreate;
		this.hasTitle = hasTitle;
	}

	
	@Override
	public RecordEntity getEntity() {
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


	@Override
	public boolean isCreatable() {
		return this.toCreate && this.hasTitle;
	}

	
	public boolean hasTitle() {
		return this.hasTitle;
	}
	
}
