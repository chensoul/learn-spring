package org.springframework.aop;

import java.lang.reflect.Method;

public class MyAfterReturningAdvice implements AfterReturningAdvice {
	@Override
	public void afterReturning(Object returnValue, Method m, Object[] args, Object target) throws Throwable {
		System.out.println("afterReturning:" + returnValue);
	}
}
