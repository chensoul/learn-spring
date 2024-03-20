package org.springframework.aop.auto;

import org.junit.Test;

public class ControlFlowFactoryTest {
	StackTraceElement[] stackTrace = new Throwable().getStackTrace();

	@Test
	public void testStackTrace() {
		a();
	}

	private void a() {
		b();
	}

	private void b() {
		// StackTraceElement[] stackTrace = new Throwable().getStackTrace();
		for (StackTraceElement traceElement : stackTrace) {
			System.out.println(traceElement.getClassName() + " --m:" + traceElement.getMethodName());
		}
		System.out.println("bb---");
	}
}
