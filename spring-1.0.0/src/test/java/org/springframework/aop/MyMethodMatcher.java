package org.springframework.aop;

import org.springframework.aop.MethodMatcher;

import java.lang.reflect.Method;

public class MyMethodMatcher implements MethodMatcher {
  @Override
  public boolean matches(Method m, Class targetClass) {
    return m.getName()
            .equals("doSth");
  }

  @Override
  public boolean isRuntime() {
    return false;//等于false不会调用3个参数的matches方法
  }

  @Override
  public boolean matches(Method m, Class targetClass, Object[] args) {
    return false;
  }
}
