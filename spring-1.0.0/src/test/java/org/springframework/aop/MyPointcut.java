package org.springframework.aop;

public class MyPointcut implements Pointcut {
  @Override
  public ClassFilter getClassFilter() {
    return new MyClassFilter();
  }

  @Override
  public MethodMatcher getMethodMatcher() {
    return new MyMethodMatcher();
  }
}
