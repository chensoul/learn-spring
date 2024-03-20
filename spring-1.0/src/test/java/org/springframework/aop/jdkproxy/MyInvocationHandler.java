package org.springframework.aop.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

	private Object origin;

	public MyInvocationHandler(Object origin) {
		this.origin = origin;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("invoke start");
		Object result = method.invoke(origin, args);
		System.out.println("invoke end");
		return result;
	}
}
