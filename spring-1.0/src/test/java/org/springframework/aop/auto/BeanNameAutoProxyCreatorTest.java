package org.springframework.aop.auto;

import org.springframework.aop.MyMethodInterceptor;
import org.springframework.aop.UserService;
import org.springframework.aop.UserServiceImpl;
import org.junit.Test;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class BeanNameAutoProxyCreatorTest {
  @Test
  public void hello() {
    DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

    BeanDefinition definition = new RootBeanDefinition(MyMethodInterceptor.class, 0);
    factory.registerBeanDefinition("myInterceptor", definition);

    BeanDefinition definition2 = new RootBeanDefinition(UserServiceImpl.class, 0);
    factory.registerBeanDefinition("userService", definition2);

    BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
    autoProxyCreator.setBeanNames(new String[]{"userService*"});
    autoProxyCreator.setInterceptorNames(new String[]{"myInterceptor"});
    autoProxyCreator.setBeanFactory(factory);

    factory.addBeanPostProcessor(autoProxyCreator);

    UserService userService = (UserService) factory.getBean("userService");
    userService.doSth();
  }
}
