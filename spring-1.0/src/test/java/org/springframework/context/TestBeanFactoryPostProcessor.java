package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		RootBeanDefinition testBean = (RootBeanDefinition) beanFactory.getBeanDefinition("testBean");
		System.out.println("调用 postProcessBeanFactory 处理：" + testBean.getBeanClassName());
	}

}
