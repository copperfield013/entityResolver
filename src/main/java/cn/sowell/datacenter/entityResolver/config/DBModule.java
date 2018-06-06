package cn.sowell.datacenter.entityResolver.config;

import java.util.Date;

public class DBModule extends TheModule{
	/**
	 * 
	 */
	private static final long serialVersionUID = -437488829597412170L;
	private Long id;
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
