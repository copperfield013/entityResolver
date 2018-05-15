package cn.sowell.datacenter.entityResolver.config;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.sowell.copframe.utils.TextUtils;

class ModuleMetaImpl implements ModuleMeta {
	private String name;
	private String title;
	private Set<String> functions = new LinkedHashSet<>();
	public ModuleMetaImpl(String name, String title) throws FusionConfigException {
		if(!TextUtils.hasText(name)) {
			throw new FusionConfigException("module节点的name字段为空");
		}
		if(!TextUtils.hasText(title)) {
			throw new FusionConfigException("module节点[name=" + name + "]的title字段为空");
		}
		this.name = name;
		this.title = title;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		throw new UnsupportedOperationException("请调用moduleMeta的getName方法");
	}
	@Override
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public boolean hasFunction(String funcName) {
		return functions.contains(funcName);
	}
	public void addFunction(String funcName) {
		functions.add(funcName);
	}
}
