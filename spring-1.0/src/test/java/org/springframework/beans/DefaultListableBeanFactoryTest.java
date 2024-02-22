//package org.springframework.beans;
//
//import java.lang.reflect.Constructor;
//import org.junit.Test;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.beans.factory.support.RootBeanDefinition;
//import org.springframework.config.TestBean;
//import org.springframework.entity.UserDaoImpl;
//import org.springframework.entity.UserServiceImpl;
//
//public class DefaultListableBeanFactoryTest {
//	@Test
//	public void testAutowireMethod() {
//		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
//		RootBeanDefinition daoDefinition = new RootBeanDefinition(TestBean.class, 0);
//		factory.registerBeanDefinition("dao", daoDefinition);
//
//		int modeAuto = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
//		int modeName = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
//		int modeType = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
//		int modeConstructor = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
//
//		//这里传递是一个Class
//		UserServiceImpl userService = (UserServiceImpl) factory.autowire(UserServiceImpl.class, modeAuto, false);//自动模式采用了属性注入方式
//		System.out.println(userService);
//		userService.insert();
//
//		userService = (UserServiceImpl) factory.autowire(UserServiceImpl.class, modeName, false);//仍然采用属性注入方式,但名字要能匹配上daoDefinition指定的名字
//		System.out.println(userService);
//		userService.insert();
//
//		userService = (UserServiceImpl) factory.autowire(UserServiceImpl.class, modeType, false); //类型注入
//		System.out.println(userService);
//		userService.insert();
//
//		userService = (UserServiceImpl) factory.autowire(UserServiceImpl.class, modeConstructor, false);
//		System.out.println(userService);
//		userService.insert();
//	}
//
//	@Test
//	public void testTypeDiffWeight() {
//		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
//		Class<UserServiceImpl> serviceClass = UserServiceImpl.class;
//		Constructor<?>[] constructors = serviceClass.getConstructors();
//		Object[] args = new Object[]{new UserDaoImpl()};
//		for (int i = 0; i < constructors.length; i++) {
//			Constructor<?> constructor = constructors[i];
//			Class<?>[] types = constructor.getParameterTypes();
//			int weight = factory.getTypeDifferenceWeight(types, args);
//			System.out.println("constructor::" + constructor + " weight:" + weight);
//		}
//	}
//
//	@Test
//	public void testAutowireBeanPropertiesMethod() {
//		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
//		RootBeanDefinition daoDefinition = new RootBeanDefinition(UserDaoImpl.class, 0);
//		factory.registerBeanDefinition("dao", daoDefinition);
//
//		//只支持 AUTOWIRE_BY_NAME and AUTOWIRE_BY_TYPE 装配模式
//
//		int modeName = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
//		int modeType = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
//
//		UserServiceImpl userService = new UserServiceImpl();
//		factory.autowireBeanProperties(userService, modeName, false);
//		System.out.println(userService);
//		userService.insert();
//
//		//这里传递的是一个对象
//		factory.autowireBeanProperties(userService, modeType, false);
//		System.out.println(userService);
//		userService.insert();
//	}
//
//
//}
