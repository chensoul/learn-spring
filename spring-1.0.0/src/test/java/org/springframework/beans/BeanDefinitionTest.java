package org.springframework.beans;

import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import static org.springframework.beans.factory.support.RootBeanDefinition.AUTOWIRE_NO;
import org.springframework.entity.UserDaoFactoryBean;
import org.springframework.entity.UserDaoImpl;
import org.springframework.entity.UserServiceImpl;

public class BeanDefinitionTest {
	@Test
	public void testWithNoConstructor() {
		RootBeanDefinition rbd = new RootBeanDefinition(UserDaoImpl.class, 0);
		rbd.setSingleton(false);

		BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("UserDaoImpl", rbd);

		Object bean = ((DefaultListableBeanFactory) registry).getBean("UserDaoImpl");
		System.out.println(bean);

		Object bean2 = ((DefaultListableBeanFactory) registry).getBean("UserDaoImpl");
		System.out.println(bean2);

		for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
			System.out.println(beanDefinitionName);
		}
	}

	@Test
	public void testWithPropertyValue() {
		RootBeanDefinition rbd = new RootBeanDefinition(UserServiceImpl.class, 0);
		rbd.setSingleton(false);

		PropertyValue pv1 = new PropertyValue("dao", new UserDaoImpl());

		rbd.getPropertyValues().addPropertyValue(pv1);

		BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("UserServiceImpl", rbd);

		Object bean = ((DefaultListableBeanFactory) registry).getBean("UserServiceImpl");
		System.out.println(bean);
	}

	@Test
	public void testWithConstructor() {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new UserDaoImpl());
		RootBeanDefinition rbd = new RootBeanDefinition(UserServiceImpl.class, cav, null);
		rbd.setSingleton(false);
		System.out.println("--- 是否有构造函数参数值设置:" + rbd.hasConstructorArgumentValues());

		BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("UserServiceImpl", rbd);

		Object bean = ((DefaultListableBeanFactory) registry).getBean("UserServiceImpl");
		System.out.println(bean);
	}

	@Test
	public void testWithConstructorAndInitDestroyMethod() {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new UserDaoImpl());
		RootBeanDefinition rbd = new RootBeanDefinition(UserServiceImpl.class, cav, null);
		rbd.setSingleton(false);
		rbd.setInitMethodName("init");
		rbd.setDestroyMethodName("destroy");

		BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("UserServiceImpl", rbd);

		Object bean = ((DefaultListableBeanFactory) registry).getBean("UserServiceImpl");
		System.out.println(bean);
	}

	@Test
	public void testWithFactoryBean() {
		RootBeanDefinition rbd = new RootBeanDefinition(UserDaoFactoryBean.class, AUTOWIRE_NO);
		BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("UserDao", rbd);

		rbd.setLazyInit(true);

		Object bean = ((DefaultListableBeanFactory) registry).getBean("UserDao");
		System.out.println(bean);
	}
}
