package org.springframework.aop;

public class Child2 implements MyInf {
  @Override
  public void doSth2() {
    System.out.println("doSth2 in child2 implement MyInf--");
  }
}
