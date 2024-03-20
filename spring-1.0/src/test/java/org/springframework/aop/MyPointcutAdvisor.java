package org.springframework.aop;

import org.aopalliance.aop.Advice;

public class MyPointcutAdvisor implements PointcutAdvisor {
	@Override
	public boolean isPerInstance() {
		return false;
	}

	@Override
	public Advice getAdvice() {
		return new MyMethodBeforeAdvice();
	}

	@Override
	public Pointcut getPointcut() {
		return new MyPointcut();
	}
}
