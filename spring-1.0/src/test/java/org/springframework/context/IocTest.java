package org.springframework.context;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IocTest {

	/*
		问题1：如何创建BeanFactory(真实类型是什么)？
	   	问题2：如何解析的配置文件，封装的BeanDefation
	   	问题3：IOC流程
	   	问题4：Bean的生命周期
	   		实例化 BeanFactoryPostProcessor
	   		执行 BeanFactoryPostProcessor 的方法
	   		实例化 BeanPostProcessor
	   	问题5：什么是循环依赖？怎么解决循环依赖？
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		TestBean testBean = (TestBean) context.getBean("testBean");
		testBean.print();
		context.close();
	}

	@Test
	public void testFactoryBean() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		TestBean testBean1 = (TestBean) context.getBean("testBean4");
		System.out.println(testBean1);

		TestBean testBean2 = (TestBean) context.getBean("testBean4");
		System.out.println(testBean2);

		context.close();
	}
}
