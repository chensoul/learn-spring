package org.springframework.context;

public class TestBeanFactory {
	public static TestBean getTestBean() {
		System.out.println("static factory getTestBean....");
		return new TestBean();
	}
}
