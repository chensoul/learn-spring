package org.springframework.beans;

public class ChildBean extends ParentBean {
	public ChildBean() {
	}

	public ChildBean(Integer id, String name) {
		super(id, name);
	}
}
