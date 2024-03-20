package org.springframework.aop.jdkproxy;

import java.lang.reflect.Proxy;
import org.springframework.aop.UserService;
import org.springframework.aop.UserServiceImpl;

public class JdkProxyTest {
	public static void main(String[] args) {
		UserService proxy = (UserService) Proxy.newProxyInstance(JdkProxyTest.class.getClassLoader(),
			new Class[]{UserService.class}, new MyInvocationHandler(new UserServiceImpl()));
		proxy.doSth();
	}
}
