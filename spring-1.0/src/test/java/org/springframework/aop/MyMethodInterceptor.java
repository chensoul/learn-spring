package org.springframework.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MyMethodInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("---" + getClass() + "---before---");
		Object ret = invocation.proceed();
		System.out.println("---" + getClass() + "---after---");
		return ret;
	}
}
