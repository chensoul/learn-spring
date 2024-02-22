package org.springframework.aop;

public class CustomAfterAdviceImpl implements CustomAfterAdvice {
  @Override
  public void after() {
    System.out.println("after---");
  }
}
