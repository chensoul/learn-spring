package org.springframework.aop.cglib;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibInterceptor implements MethodInterceptor {

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("intercept start");
		Object result = proxy.invokeSuper(obj, args);
		System.out.println("intercept end");
		return result;
	}
}
