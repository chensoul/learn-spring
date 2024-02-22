package org.springframework.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class TestBean implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {
	private String name;

	private BeanFactory beanFactory;
	private String beanName;

	public TestBean() {
		System.out.println("TestBean默认构造器");
	}

	public TestBean(String name) {
		this.name = name;
		System.out.println("TestBean构造器注入name");
	}

	public void print() {
		System.out.println("print方法");
	}

	public void setName(String name) {
		System.out.println("注入属性name");
		this.name = name;
	}

	// 这是BeanFactoryAware接口方法
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("BeanFactoryAware接口调用setBeanFactory()");
		this.beanFactory = beanFactory;
	}

	// 这是BeanNameAware接口方法
	@Override
	public void setBeanName(String name) {
		System.out.println("BeanNameAware接口调用setBeanName()");
		this.beanName = name;
	}

	// 通过<bean>的destroy-method属性指定的初始化方法
	@Override
	public void destroy() throws Exception {
		System.out.println("调用DisposableBean的destroy方法：" + this);
	}

	public void init() {
		System.out.println("调用<bean>的init-method属性指定的初始化方法");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("调用InitializingBean.afterPropertiesSet()");
	}
}
