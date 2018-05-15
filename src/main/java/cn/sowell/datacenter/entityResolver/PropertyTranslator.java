package cn.sowell.datacenter.entityResolver;

public interface PropertyTranslator<T, V> {
	boolean check(T propValue);
	V transfer(T propValue);

}
