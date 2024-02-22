package org.springframework.aop.auto;

import org.springframework.aop.MyMethodInterceptor;
import org.springframework.aop.UserService;
import org.springframework.aop.UserServiceImpl;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 1指定一个DefaultAdvisorAutoProxyCreator Bean的定义.
 * 2.指定在相同或相关的上下文中任意数量的Advisor.注意,必须是Advisor，
 * 而不仅仅是interceptor或advice,因为必须有一个切点被评估,以便检查每个advice到候选bean定义是否合格.
 */
public class DefaultAdvisorAutoProxyCreatorTest {
  @Test
  public void hello() {
    DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

    BeanDefinition userServiceDefinition = new RootBeanDefinition(UserServiceImpl.class, 0);
    factory.registerBeanDefinition("userService", userServiceDefinition);

    //必须以下面的方式来注册NameMatchMethodPointcutAdvisor
    BeanDefinition advisorDefinition = new RootBeanDefinition(NameMatchMethodPointcutAdvisor.class, 0);
    advisorDefinition.getPropertyValues()
            .addPropertyValue("mappedName", "doSth");
    advisorDefinition.getPropertyValues()
            .addPropertyValue("advice", new MyMethodInterceptor());

    //1)不能这样注册,因为其直接放到内部的单例map缓存中了,所以DefaultAdvisorAutoProxyCreator就不能识别这个Advisor
    //    Advisor advisor = createNameMatchMethodPointcutAdvisor();
    //    factory.registerSingleton("myAdvisor", advisor);
    //2) 也不能这样,岁日advisor对象有设置属性值,但beanFactory不知道,因为实例化RootBeanDefinition时只给的advisor的class,而不是advisor对象==========================
    //    Advisor advisor = createNameMatchMethodPointcutAdvisor();
    //    BeanDefinition advisorDefinition = new RootBeanDefinition(advisor.getClass(), 0);
    //    factory.registerBeanDefinition("myAdvisor", advisorDefinition);
    //2)===================================
    factory.registerBeanDefinition("myAdvisor", advisorDefinition);

    //以注册bean的形式注册DefaultAdvisorAutoProxyCreator并不能让此自动代理创建者生效,必须以添加BPP的方式
    //    RootBeanDefinition autoProxyCreatorDefinition = new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class,0);
    //    factory.registerBeanDefinition("autoCreator", autoProxyCreatorDefinition);

    DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
    autoProxyCreator.setBeanFactory(factory);

    factory.addBeanPostProcessor(autoProxyCreator);

    UserService userService = (UserService) factory.getBean("userService");
    userService.doSth();
  }


  private Advisor createNameMatchMethodPointcutAdvisor() {
    NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
    advisor.setMappedName("doSth");
    advisor.setAdvice(new MyMethodInterceptor());
    return advisor;
  }
}
