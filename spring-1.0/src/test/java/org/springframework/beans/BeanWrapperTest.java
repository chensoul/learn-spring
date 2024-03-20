package org.springframework.beans;

import java.lang.reflect.Constructor;
import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.context.TestBean;

public class BeanWrapperTest {
	@Test
	public void testBeanWrapper() {
		BeanWrapper beanWrapper = new BeanWrapperImpl(EmptyBean.class);

		Object instance = beanWrapper.getWrappedInstance();
		System.out.println(instance);
	}

	@Test
	public void testPropertyEditor1() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(ParentBean.class);
		Object instance = beanWrapper.doTypeConversionIfNecessary("111", Integer.class);
		System.out.println(instance);
	}

	@Test
	public void testPropertyEditor2() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl();
		Object instance = beanWrapper.doTypeConversionIfNecessary("org.springframework.beans.ParentBean", ParentBean.class.getClass());
		System.out.println(instance);
		System.out.println(instance.getClass().getName());
	}

	@Test
	public void testPropertyEditor3() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(MyEntity.class);
		PropertyValue p1 = new PropertyValue("clz", "org.springframework.beans.ParentBean");

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(p1);

		beanWrapper.setPropertyValues(pvs);
		Object instance = beanWrapper.getWrappedInstance();
		System.out.println(instance);
	}

	@Test
	public void testBeanWrapperPropertyValue() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(ParentBean.class);
		PropertyValue p1 = new PropertyValue("id", 111);
		PropertyValue p2 = new PropertyValue("name", "test");

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(p1);
		pvs.addPropertyValue(p2);

		System.out.println(pvs.getPropertyValue("id"));
		System.out.println(pvs.getPropertyValue("name"));

		beanWrapper.setPropertyValues(pvs);
		Object instance = beanWrapper.getWrappedInstance();
		System.out.println(instance);
	}

	@Test
	public void testConstructorArgumentValues() {
		//下面的构造函数设置可以满足(String,double)这样的构造函数
		ConstructorArgumentValues cargs = new ConstructorArgumentValues();
		cargs.addIndexedArgumentValue(1, 10, "double");
		cargs.addGenericArgumentValue("test");
		ConstructorArgumentValues.ValueHolder holder = cargs.getArgumentValue(0, Double.TYPE);
		System.out.println(holder);

		ConstructorArgumentValues.ValueHolder holder2 = cargs.getArgumentValue(0, String.class);
		System.out.println(holder2.getValue());
	}

	@Test
	public void testBeanUtils() throws Exception {
		Object o = BeanUtils.instantiateClass(ParentBean.class);
		System.out.println(o);

		Class<ParentBean> parentBeanClass = ParentBean.class;
		Constructor<ParentBean> constructor = parentBeanClass.getConstructor(Integer.TYPE, String.class);
		Object[] constructorArgValues = {111, "test"};
		Object o1 = BeanUtils.instantiateClass(constructor, constructorArgValues);
		System.out.println(o1);
	}

	@Test
	public void testBeanUtilsAndConstructorArgumentValues() throws Exception {
		ConstructorArgumentValues cargs = new ConstructorArgumentValues();
		cargs.addIndexedArgumentValue(0, 111, "int");
		cargs.addGenericArgumentValue("test");

		Class<ParentBean> parentBeanClass = ParentBean.class;
		Constructor<ParentBean> constructor = parentBeanClass.getConstructor(Integer.TYPE, String.class);
		Object[] constructorArgValues = {cargs.getArgumentValue(0, Integer.TYPE).getValue(), cargs.getArgumentValue(1, String.class).getValue()};
		Object o1 = BeanUtils.instantiateClass(constructor, constructorArgValues);
		System.out.println(o1);
	}

	@Test
	public void testBeanUtilsAndConstructorArgumentValues2() throws Exception {

		ConstructorArgumentValues cargs = new ConstructorArgumentValues();
		cargs.addGenericArgumentValue("test");

		Class<TestBean> userClass = TestBean.class;
		Constructor<TestBean> constructor = userClass.getConstructor(String.class);
		//查找第一个参数值时,由于索引位置0没有索引类型参数,就查通用类型参数,所以可以找到test这个参数值
		Object[] constructorArgValues = {cargs.getArgumentValue(0, String.class).getValue()};
		Object o1 = BeanUtils.instantiateClass(constructor, constructorArgValues);
		System.out.println(o1);

	}

}
