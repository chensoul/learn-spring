package org.springframework.aop;

import org.aopalliance.intercept.Interceptor;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

public class AdviceAndAdvisorTest {
	@Test
	public void testMethodInterceptor() {
		UserService service = new UserServiceImpl();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);
		proxyFactory.addInterceptor(new MyMethodInterceptor());

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		System.out.println(proxy.getClass().getName());
	}


	@Test
	public void testAfterReturnningAdvice() {
		UserService service = new UserServiceImpl();
		AfterReturningAdvice afterReturningAdvice = new MyAfterReturningAdvice();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);

		AfterReturningAdviceInterceptor interceptor = new AfterReturningAdviceInterceptor(afterReturningAdvice);
		// proxyFactory提供了添加MethodBeforeAdvice,ThrowAdvice通知的功能,独独没有没有提供AfterReturningAdvice功能
		proxyFactory.addInterceptor(interceptor);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		System.out.println(proxy.getClass().getName());
	}

	@Test
	public void testMethodBeforeAdvice() {
		UserService service = new UserServiceImpl();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);

		proxyFactory.addBeforeAdvice(new MyMethodBeforeAdvice());

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		System.out.println(proxy.getClass()
			.getName());
	}

	@Test
	public void testCustomAfterAdvice() {
		UserService service = new UserServiceImpl();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);

		CustomAfterAdvice afterAdvice = new CustomAfterAdviceImpl();
		CustomAfterAdviceInterceptor afterAdviceInterceptor = new CustomAfterAdviceInterceptor(afterAdvice);

		proxyFactory.addInterceptor(afterAdviceInterceptor);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		System.out.println(proxy.getClass().getName());
	}

	@Test
	public void testAdvisor() {
		UserService service = new UserServiceImpl();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);

		MyMethodBeforeAdvice advice = new MyMethodBeforeAdvice();
		NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor(advice);
		advisor.setMappedName("doSth");//只有doSth会被拦截,insert方法不会

		proxyFactory.addAdvisor(advisor);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		proxy.insert();
		System.out.println(proxy.getClass().getName());
	}

	@Test
	public void testAdvisorAdapter() {
		UserService service = new UserServiceImpl();

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(service);

		MyMethodBeforeAdvice advice = new MyMethodBeforeAdvice();
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(advice);
		Interceptor interceptor = GlobalAdvisorAdapterRegistry.getInstance().getInterceptor(advisor);

		proxyFactory.addInterceptor(interceptor);

		UserService proxy = (UserService) proxyFactory.getProxy();
		proxy.doSth();
		proxy.insert();
		System.out.println(proxy.getClass().getName());
	}
}
