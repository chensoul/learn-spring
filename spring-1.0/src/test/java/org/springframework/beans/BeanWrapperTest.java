package org.springframework.beans;

import java.lang.reflect.Constructor;
import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.config.TestBean;

public class BeanWrapperTest {


	@Test
	public void testBeanWrapper() {
		BeanWrapper beanWrapper = new BeanWrapperImpl(TestBean.class);

		Object instance = beanWrapper.getWrappedInstance();
		System.out.println(instance);
	}

	@Test
	public void testBeanWrapperTypeConversion() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(TestBean.class);
		Object o = beanWrapper.doTypeConversionIfNecessary("11", Double.class);
		System.out.println(o); //输出11.0
		System.out.println(o.getClass().getName());//输出:java.lang.Double
	}

	@Test
	public void testBeanWrapperPropertyEditor() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl();
		//A.class.getClass()得到的是A的Class对象
		Object o = beanWrapper.doTypeConversionIfNecessary("org.springframework.config.TestBean", TestBean.class.getClass());
		System.out.println(o);
		System.out.println(o.getClass().getName());
	}

	@Test
	public void testBeanWrapperPropertyEditor2() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(TestBean.class);
		PropertyValue p1 = new PropertyValue("name", "111");

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(p1);

		beanWrapper.setPropertyValues(pvs);
		Object instance = beanWrapper.getWrappedInstance();
		System.out.println(instance);//输出：MyEntity{clz=class entity.factorybean.A}
	}

	@Test
	public void testPropertyValue() {
		PropertyValue p2 = new PropertyValue("name", "cj");

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(p2);

		System.out.println(pvs.getPropertyValue("name"));

		BeanWrapper beanWrapper = new BeanWrapperImpl(TestBean.class);
		beanWrapper.setPropertyValues(pvs);

		Object instance = beanWrapper.getWrappedInstance();
		System.out.println(instance);

	}

	@Test
	public void testMutablePropertyValues() {
		PropertyValue p2 = new PropertyValue("name", "cj");

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(p2);

		PropertyValue p3 = new PropertyValue("id", 20);
		pvs.setPropertyValueAt(p3, 0);

		System.out.println(pvs.getPropertyValue("name"));
	}


	@Test
	public void testConstructorArgumentValues() {
		//ConstructorArgumentValues类型在AbstractAutowireCapableBeanFactory
		// 的autowireConstrutor方法中有读取其值进行bean实例化的使用
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addIndexedArgumentValue(0, 10, "double");
		cav.addIndexedArgumentValue(1, "abc");
		//这里必须是double
		ConstructorArgumentValues.ValueHolder valueHolder = cav.getIndexedArgumentValue(0, Double.TYPE);
		System.out.println(valueHolder);
		System.out.println(valueHolder.getValue());
		System.out.println(valueHolder.getType());
		System.out.println(valueHolder.getClass());
		System.out.println("=================================================");
		ConstructorArgumentValues.ValueHolder valueHolder2 = cav.getIndexedArgumentValue(1, String.class);
		System.out.println(valueHolder2);
		System.out.println(valueHolder2.getValue());
		System.out.println(valueHolder2.getType());
		System.out.println(valueHolder2.getClass());
	}

	@Test
	public void testConstructorArgumentValues2() {
		//下面的构造函数设置可以满足(String,double)这样的构造函数
		ConstructorArgumentValues cargs = new ConstructorArgumentValues();
		cargs.addIndexedArgumentValue(1, 10, "double");
		cargs.addGenericArgumentValue("cj");
		ConstructorArgumentValues.ValueHolder holder = cargs.getArgumentValue(0, Double.TYPE);
		System.out.println(holder);

		ConstructorArgumentValues.ValueHolder holder2 = cargs.getArgumentValue(0, String.class);
		System.out.println(holder2.getValue());

	}

	@Test
	public void testBeanUtils() throws Exception {
		Object o = BeanUtils.instantiateClass(TestBean.class);
		System.out.println(o);

		Class<TestBean> userClass = TestBean.class;
		Constructor<TestBean> constructor = userClass.getConstructor(String.class);
		Object[] constructorArgValues = {"test"};
		Object o1 = BeanUtils.instantiateClass(constructor, constructorArgValues);
		System.out.println(o1);
	}

	@Test
	public void testBeanUtilsAndConstructorArgumentValues() throws Exception {

		ConstructorArgumentValues cargs = new ConstructorArgumentValues();
		cargs.addGenericArgumentValue("test");

		Class<TestBean> userClass = TestBean.class;
		Constructor<TestBean> constructor = userClass.getConstructor(String.class);
		Object[] constructorArgValues = {cargs.getArgumentValue(0, String.class).getValue()};
		Object o1 = BeanUtils.instantiateClass(constructor, constructorArgValues);
		System.out.println(o1);

	}

	@Test
	public void testBeanUtilsAndConstructorArgumentValues2() throws Exception {

		ConstructorArgumentValues cargs = new ConstructorArgumentValues();
		cargs.addGenericArgumentValue("test");

		Class<TestBean> userClass = TestBean.class;
		Constructor<TestBean> constructor = userClass.getConstructor(String.class);
		//查找第一个参数值时,由于索引位置0没有索引类型参数,就查通用类型参数,所以可以找到cj这个参数值
		Object[] constructorArgValues = {cargs.getArgumentValue(0, String.class).getValue()};
		Object o1 = BeanUtils.instantiateClass(constructor, constructorArgValues);
		System.out.println(o1);

	}

	@Test
	public void testPropertyEditor() {

		//TODO: 见AbstractBeanFactory的initBeanWrapper方法,417行
	}
}
