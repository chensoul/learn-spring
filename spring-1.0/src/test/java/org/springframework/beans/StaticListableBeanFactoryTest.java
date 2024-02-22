package org.springframework.beans;

import org.springframework.entity.UserServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

public class StaticListableBeanFactoryTest {
  @Test
  public void hello() {
    StaticListableBeanFactory factory = new StaticListableBeanFactory();
    factory.addBean("service", new UserServiceImpl());//注册bean
    UserServiceImpl instance = (UserServiceImpl) factory.getBean("service");
    instance.insert();
  }
}
