package org.springframework.context;

import org.springframework.beans.factory.FactoryBean;

public class TestBeanFactoryBean implements FactoryBean {
	public TestBean getObject() throws Exception {
		System.out.println("FactoryBean getObject....");

		return new TestBean();
	}

	public Class<?> getObjectType() {
		return TestBean.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
