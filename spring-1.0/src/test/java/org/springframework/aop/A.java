package org.springframework.aop;

import java.util.LinkedList;

//演示递归形式实现环绕
public class A {

  private Object target;
  int i = 0;
  LinkedList<B> list = new LinkedList<>();

  public A() {
    B b = new B(1);
    B b2 = new B(2);
    list.add(b);
    list.add(b2);
  }

  public void execute() {

    if (list.isEmpty()) {
      System.out.println("invoke target");
    } else {
      B pop = list.pop();
      pop.doSth(this);
    }
  }

  public static void main(String[] args) {
    new A().execute();
  }
}


class B {
  private int i;

  public B(int i) {
    this.i = i;
  }

  public void doSth(A a) {
    System.out.println(i + "before");
    a.execute();
    System.out.println(i + "after");
  }
}
