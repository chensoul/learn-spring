package org.springframework.context;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.cyclic.TestService1;
import org.springframework.cyclic.TestService2;

public class IocCyclicTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext =
			new ClassPathXmlApplicationContext("classpath:applicationContext-cyclic.xml");

		TestService1 testService1 = (TestService1) applicationContext.getBean("testService1");
		TestService2 testService2 = (TestService2) applicationContext.getBean("testService2");
		testService1.aTest();
		testService2.aTest();
	}

}
