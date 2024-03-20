package org.springframework.aop;

import java.lang.reflect.Method;
import org.junit.Test;
import org.springframework.aop.support.RegexpMethodPointcut;

public class PointcutTest {
	@Test
	public void testDeclareClassAndTargetClassDiff() throws Exception {
		Child c = new Child();
		Class<? extends Child> aClass = c.getClass();
		Method method = aClass.getMethod("doSth");
		//doSth定义在了其父类Parent中
		System.out.println(method.getDeclaringClass());//是Parent

		Child2 child2 = new Child2();
		Class<? extends Child2> child2Class = child2.getClass();
		Method child2ClassMethod = child2Class.getMethod("doSth2");
		//输出的是Child2,不是接口
		System.out.println(child2ClassMethod.getDeclaringClass());
	}

	@Test
	public void testRegexpMethodPoint() throws Exception {
		Child c = new Child();
		RegexpMethodPointcut pointcut = new RegexpMethodPointcut();
		pointcut.setPattern("org.springframework.aop.Parent.d[a-zA-Z]{3}h");
		Class<? extends Child> aClass = c.getClass();
		Method method = c.getClass().getMethod("doSth");
		boolean matches = pointcut.matches(method, aClass);
		System.out.println(matches);
	}
}
