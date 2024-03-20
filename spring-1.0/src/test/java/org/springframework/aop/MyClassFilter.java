package org.springframework.aop;

public class MyClassFilter implements ClassFilter {
	@Override
	public boolean matches(Class clazz) {
		return clazz.equals(UserServiceImpl.class);
	}
}
