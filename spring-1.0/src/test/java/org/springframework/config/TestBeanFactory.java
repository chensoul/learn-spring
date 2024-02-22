package org.springframework.config;

public class TestBeanFactory {
	public static TestBean getTestBean() {
		System.out.println("static factory getTestBean....");
		return new TestBean();
	}
}
