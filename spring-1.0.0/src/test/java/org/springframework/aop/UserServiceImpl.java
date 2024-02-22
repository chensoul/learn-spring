package org.springframework.aop;

import org.springframework.aop.framework.AopContext;

public class UserServiceImpl implements UserService {
  @Override
  public void doSth() {
    System.out.println("doSth in UserServiceImpl");
  }

  @Override
  public void doSth2() {
    System.out.println("doSth2 in UserServiceImpl");
    System.out.println("=============");
    //insert();
    //测试暴露代理对象的用法,exposeProxy=true,AopContext.currentProxy()
    ((UserService) AopContext.currentProxy()).insert();
  }


  @Override
  public void insert() {
    System.out.println("insert in UserServiceImpl");
  }
}
