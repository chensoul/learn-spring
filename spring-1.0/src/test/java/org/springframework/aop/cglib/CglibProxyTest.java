package org.springframework.aop.cglib;

import net.sf.cglib.proxy.Enhancer;
import org.springframework.aop.UserServiceImpl;

public class CglibProxyTest {

	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(UserServiceImpl.class);
		enhancer.setCallback(new CglibInterceptor());
		UserServiceImpl proxy = (UserServiceImpl) enhancer.create();
		proxy.doSth();
	}
}
