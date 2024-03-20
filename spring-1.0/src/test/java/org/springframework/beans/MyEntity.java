package org.springframework.beans;

public class MyEntity {
	private Class clz;

	public Class getClz() {
		return clz;
	}

	public void setClz(Class clz) {
		this.clz = clz;
	}

	@Override
	public String toString() {
		return "MyEntity{" +
			   "clz=" + clz +
			   '}';
	}
}
