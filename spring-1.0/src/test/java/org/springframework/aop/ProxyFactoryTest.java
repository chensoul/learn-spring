package org.springframework.aop;

import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ProxyFactoryTest {
	@Test
	public void testProxyFactory() {
		UserService service = new UserServiceImpl();
		MethodBeforeAdvice beforeAdvice = new MyMethodBeforeAdvice();

		ProxyFactory proxyFactory = new ProxyFactory();
		//proxyFactory.setTarget(service);
		//不加下面的代码用的是cglib方式生成代理
		proxyFactory.setInterfaces(new Class[]{UserService.class});
		proxyFactory.addBeforeAdvice(beforeAdvice);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		proxy.insert();
		System.out.println(proxy.getClass().getName());
	}

	@Test
	public void testExposeProxy() {
		UserService service = new UserServiceImpl();
		MethodBeforeAdvice beforeAdvice = new MyMethodBeforeAdvice();

		ProxyFactory proxyFactory = new ProxyFactory();
		//设置暴露代理
		proxyFactory.setExposeProxy(true);
		proxyFactory.setTarget(service);
		//不加下面的代码用的是cglib方式生成代理
		proxyFactory.setInterfaces(new Class[]{UserService.class});
		proxyFactory.addBeforeAdvice(beforeAdvice);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth2();
		System.out.println(proxy.getClass().getName());
	}

	@Test
	public void testAdvised() {
		UserService service = new UserServiceImpl();
		MethodBeforeAdvice beforeAdvice = new MyMethodBeforeAdvice();

		//构造函数传入target,会主动解析接口,有的话就会用jdk生成代理类
		ProxyFactory proxyFactory = new ProxyFactory(service);
		proxyFactory.addBeforeAdvice(beforeAdvice);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		//System.out.println(proxy.getClass().getName());

		System.out.println("===========操作Advised接口,添加额外的拦截器并再次调用方法===========");
		//代理的接口除了设置的UserService接口还有Advised接口
		Advised advisedProxy = (Advised) proxy;
		//System.out.println(advisedProxy.toProxyConfigString());

		advisedProxy.addInterceptor(new MyMethodInterceptor());
		proxy.doSth();

	}

	@Test
	public void testProxyFactoryBean() {
		ClassPathResource resource = new ClassPathResource("aop.xml");
		XmlBeanFactory factory = new XmlBeanFactory(resource);
		// 获取的是FactoryBean
		UserService userServiceProxy = (UserService) factory.getBean("userServiceProxy");
		userServiceProxy.doSth();
		userServiceProxy.insert();
		System.out.println(userServiceProxy.getClass().getName());
	}


	@Test
	public void testBeanNameAutoProxyCreator() {
		//    ClassPathResource resource = new ClassPathResource("aop.xml");
		//    XmlBeanFactory factory = new XmlBeanFactory(resource);
		ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("classpath:aop.xml");
		// 直接找目标对象
		UserService userServiceProxy = (UserService) factory.getBean("userService");
		userServiceProxy.doSth();
		userServiceProxy.insert();
		System.out.println(userServiceProxy.getClass()
			.getName());
	}

	@Test
	public void testDefaultAdvisorAutoProxyCreator() {
		//    ClassPathResource resource = new ClassPathResource("aop.xml");
		//    XmlBeanFactory factory = new XmlBeanFactory(resource);
		ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("classpath:aop.xml");
		// 直接找目标对象
		UserServiceImpl2 userServiceProxy = (UserServiceImpl2) factory.getBean("userService");
		userServiceProxy.doSth();
		userServiceProxy.insert();
		System.out.println(userServiceProxy.getClass().getName());
	}


	@Test
	public void hello2() {
		UserServiceImplWithoutImplInterface service = new UserServiceImplWithoutImplInterface();
		MethodBeforeAdvice beforeAdvice = new MyMethodBeforeAdvice();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);
		proxyFactory.addBeforeAdvice(beforeAdvice);

		UserServiceImplWithoutImplInterface proxy = (UserServiceImplWithoutImplInterface) proxyFactory.getProxy();
		proxy.doSth();
		System.out.println(proxy.getClass().getName());

	}

	@Test
	public void testAdvisedSupport() {
		AdvisedSupport advisedSupport = new AdvisedSupport();
		advisedSupport.setInterfaces(new Class[]{UserService.class});
		advisedSupport.addBeforeAdvice(new MyMethodBeforeAdvice());
		advisedSupport.setTarget(new UserServiceImpl());

		UserService service = (UserService) advisedSupport.getAopProxyFactory()
			.createAopProxy(advisedSupport)
			.getProxy();

		service.doSth();
	}


	@Test//测试直接操作AdvisedSupport这个父类而不是其子类ProxyFactory
	public void testAdvisedSupportAndJdkAopProxy() {
		AdvisedSupport advisedSupport = new AdvisedSupport();
		advisedSupport.setInterfaces(new Class[]{UserService.class});
		advisedSupport.addBeforeAdvice(new MyMethodBeforeAdvice());
		advisedSupport.setTarget(new UserServiceImpl());

		AopProxyFactory aopProxyFactory = advisedSupport.getAopProxyFactory();
		AopProxy aopProxy = aopProxyFactory.createAopProxy(advisedSupport);

		UserService service = (UserService) aopProxy.getProxy();
		service.doSth();
	}

	@Test//测试添加Advisor
	public void testNameMatchMethodPointcutAdvisor() {
		ProxyFactory proxyFactory = new ProxyFactory(new UserServiceImpl());

		NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
		advisor.setMappedName("doSth");
		advisor.setAdvice(new MyMethodBeforeAdvice());

		proxyFactory.addAdvisor(advisor);

		UserService service = (UserService) proxyFactory.getProxy();
		service.doSth();
	}

	@Test
	public void testAddAdvisorWithCustomClassFilterAndMethodMatcher() {
		AdvisedSupport advisedSupport = new AdvisedSupport();
		advisedSupport.setInterfaces(new Class[]{UserService.class});
		//添加了一个自定义的Advisor
		advisedSupport.addAdvisor(new MyPointcutAdvisor());

		advisedSupport.setTarget(new UserServiceImpl());

		AopProxyFactory aopProxyFactory = advisedSupport.getAopProxyFactory();
		AopProxy aopProxy = aopProxyFactory.createAopProxy(advisedSupport);

		UserService service = (UserService) aopProxy.getProxy();
		service.doSth();

	}


	@Test
	public void testProxyFactoryBeanForSourceCodeResearch() {
		ClassPathResource resource = new ClassPathResource("aop.xml");
		XmlBeanFactory factory = new XmlBeanFactory(resource);
		// 获取的是FactoryBean
		UserService userServiceProxy = (UserService) factory.getBean("userServiceProxy");
		userServiceProxy.doSth();
		userServiceProxy.insert();
		System.out.println(userServiceProxy.getClass().getName());
	}
}
