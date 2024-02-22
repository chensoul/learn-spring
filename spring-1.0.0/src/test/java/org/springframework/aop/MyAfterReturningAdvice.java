package org.springframework.aop;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

public class MyAfterReturningAdvice implements AfterReturningAdvice {
  @Override
  public void afterReturning(Object returnValue, Method m, Object[] args, Object target) throws Throwable {
    System.out.println("return:---" + returnValue);
  }
}
