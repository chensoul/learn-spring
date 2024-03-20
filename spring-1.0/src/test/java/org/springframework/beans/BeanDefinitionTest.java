package org.springframework.beans;

import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import static org.springframework.beans.factory.support.RootBeanDefinition.AUTOWIRE_NO;
import org.springframework.context.TestBean;
import org.springframework.context.TestBeanFactoryBean;

public class BeanDefinitionTest {
	@Test
	public void testRootBeanDefinition() {
		RootBeanDefinition rbd = new RootBeanDefinition(EmptyBean.class, 0);
		rbd.setSingleton(false);

		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("EmptyBean", rbd);

		Object bean = registry.getBean("EmptyBean");
		System.out.println(bean);

		for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
			System.out.println(beanDefinitionName);
		}
	}

	@Test
	public void testChildBeanDefinition() {
		RootBeanDefinition rbd = new RootBeanDefinition(ParentBean.class, 0);
		ChildBeanDefinition cbd = new ChildBeanDefinition("p", null);
		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("p", rbd);
		registry.registerBeanDefinition("c", cbd);

		Object bean = registry.getBean("c");
		System.out.println(bean);
	}

	@Test
	public void testWithPropertyValue() {
		RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class, 0);
		rbd.setSingleton(false);

		PropertyValue pv1 = new PropertyValue("name", "test");

		rbd.getPropertyValues().addPropertyValue(pv1);

		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("TestBean", rbd);

		Object bean = registry.getBean("TestBean");
		System.out.println(bean);
	}

	@Test
	public void testWithConstructor() {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(new TestBean());
		RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class, cav, null);
		rbd.setSingleton(false);
		System.out.println("--- 是否有构造函数参数值设置:" + rbd.hasConstructorArgumentValues());

		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("TestBean", rbd);

		Object bean = registry.getBean("TestBean");
		System.out.println(bean);
	}

	@Test
	public void testWithConstructorAndInitDestroyMethod() {
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue("test");
		RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class, cav, null);
		rbd.setSingleton(false);
		rbd.setInitMethodName("init");
		rbd.setDestroyMethodName("destroy");

		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("TestBean", rbd);

		Object bean = registry.getBean("TestBean");
		System.out.println(bean);
	}

	@Test
	public void testWithFactoryBean() {
		RootBeanDefinition rbd = new RootBeanDefinition(TestBeanFactoryBean.class, AUTOWIRE_NO);
		DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
		registry.registerBeanDefinition("TestBean", rbd);

		rbd.setLazyInit(true);

		Object bean = registry.getBean("TestBean");
		System.out.println(bean);
	}
}
