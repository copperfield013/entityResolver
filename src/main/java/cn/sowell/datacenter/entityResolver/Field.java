package cn.sowell.datacenter.entityResolver;

public interface Field {

	Integer getId();

	String getTitle();

	String getAbcType();

	String getFullKey();

	String getFieldAccess();
	
	Composite getComposite();
	
	public static final String ACCESS_READ = "读"
			,ACCESS_WRITE = "写"
			,ACCESS_SUPPLY = "补"
			,ACCESS_ADD = "增"
			,ACCESS_UNION = "并";

}
