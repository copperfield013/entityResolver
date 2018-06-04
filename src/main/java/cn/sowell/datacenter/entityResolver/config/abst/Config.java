package cn.sowell.datacenter.entityResolver.config.abst;

import java.io.Serializable;
import java.util.Set;

public interface Config extends Serializable {

	Set<Module> getModules();

}