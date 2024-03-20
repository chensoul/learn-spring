package org.springframework.aop.reflect;

import org.junit.Test;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public class ClassTest {
	@Test
	public void testClass() throws ClassNotFoundException {
		Class<?> aClass = Class.forName("org.springframework.aop.reflect.ClassTest");
		System.out.println(aClass);
	}

	@Test
	public void test() {

	}
}
