package org.springframework.aop.classloader;

import org.junit.Test;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public class ClassloaderTest {
	@Test
	public void testSystemClassLoader() {
		System.out.println(ClassLoader.getSystemClassLoader());

		ClassLoader classLoader = ClassloaderTest.class.getClassLoader();
		System.out.println(classLoader);
		System.out.println(classLoader.getParent());
		System.out.println(classLoader.getParent().getParent());

		classLoader = Thread.currentThread().getContextClassLoader();
		System.out.println(classLoader);
		System.out.println(classLoader.getParent());
		System.out.println(classLoader.getParent().getParent());
	}
}
