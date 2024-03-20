package org.springframework.aop;

import java.lang.reflect.Method;

public class MyMethodBeforeAdvice implements MethodBeforeAdvice {
	@Override
	public void before(Method m, Object[] args, Object target) throws Throwable {
		System.out.println("***before after?  ^_^***");
	}
}
