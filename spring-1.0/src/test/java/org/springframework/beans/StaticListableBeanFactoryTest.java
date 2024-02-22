package org.springframework.beans;

import org.junit.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.config.TestBean;

public class StaticListableBeanFactoryTest {
	@Test
	public void hello() {
		StaticListableBeanFactory factory = new StaticListableBeanFactory();
		factory.addBean("TestBean", new TestBean());//注册bean
		TestBean instance = (TestBean) factory.getBean("TestBean");
		instance.print();
	}
}
