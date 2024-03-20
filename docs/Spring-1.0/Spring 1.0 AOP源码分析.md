## 基础储备

### Java ClassLoader

Java的ClassLoader（类加载器）机制是Java虚拟机（JVM）用于加载Java类的一种机制。它负责在运行时查找、加载和链接Java类，并生成对应的Class对象。

#### 类继承结构

ClassLoader 机制的主要目标是实现Java的动态扩展性和代码的隔离性。它允许开发人员加载来自不同来源的类，例如本地文件系统、网络、JAR文件等，并将它们组织成一个类层次结构。

ClassLoader 类继承结构：

- ClassLoader
  - SecureClassLoader
    - URLClassLoader
      - AppClassLoader
      - ExtClassLoader
      - FactoryURLClassLoader

常见的ClassLoader包括：

1. Bootstrap ClassLoader：也称为原生类加载器，它是JVM的一部分并且是使用 native 代码编写，负责加载JVM运行时，如`java.lang.Object`。Bootstrap ClassLoader 是所有 ClassLoader 的父类。
2. Extension ClassLoader：`ExtClassLoader`，用于加载Java的扩展类库，位于`jre/lib/ext`目录中 或者由 `java.ext.dirs` 指定目录下的JAR文件。
3. System ClassLoader：`AppClassLoader`，也称为应用程序类加载器，加载用户自定义的类和第三方类库。

`AppClassLoader`是默认的类加载器，如果类加载时我们不指定类加载器的情况下，默认会使用`AppClassLoader`加载类，`ClassLoader.getSystemClassLoader()`返回的系统类加载器也是`AppClassLoader`。

```java
@Test
public void testSystemClassLoader() {
  System.out.println(ClassLoader.getSystemClassLoader());

  ClassLoader classLoader = ClassloaderTest.class.getClassLoader();
  System.out.println(classLoader);
  System.out.println(classLoader.getParent());
  System.out.println(classLoader.getParent().getParent());
}
```

打印结果：

```bash
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$ExtClassLoader@cc34f4d
null
```

#### 三个特性

1. 双亲委派模型（Parent Delegation Model）：ClassLoader采用了双亲委派模型，这是ClassLoader的核心特性之一。根据该模型，当ClassLoader加载类时，它首先将加载请求委托给其父类加载器。只有在父加载器无法找到所需类时，子加载器才会尝试加载。这种层次结构的加载机制确保类的一致性和安全性，并避免重复加载。
2. 类的可见性和隔离性：ClassLoader提供了类的可见性和隔离性。每个ClassLoader实例都维护自己的类命名空间，它只能看到自己加载的类和其父加载器加载的类。这种隔离性使得不同的ClassLoader可以加载同名但内容不同的类。因此，不同的模块或应用程序可以使用自己的ClassLoader加载和管理它们的类，实现了类的隔离和冲突解决。
3. 动态加载与运行时扩展：ClassLoader允许在运行时动态加载类和资源。通过使用ClassLoader的实现，可以从不同的源（如文件系统、网络等）加载类文件。这种动态加载的能力使得Java应用程序可以在运行时根据需要加载和使用类，实现灵活性和可扩展性。

#### 上下文类加载器

有时当 JVM 核心类需要动态加载应用程序开发人员提供的类或资源时，我们可能会遇到问题。

例如，在 JNDI 中，核心功能是由 `rt.jar` 中的引导类实现的。但这些 JNDI 类可能会加载由独立供应商实现的 JNDI 提供程序（部署在应用程序类路径中）。这种情况需要引导类加载器（父类加载器）来加载应用程序加载器（子类加载器）可见的类。

**J2SE 委托在这里不起作用，为了解决这个问题，我们需要找到类加载的替代方法。这可以使用线程上下文加载器来实现。**

`java.lang.Thread` 类有一个方法 `getContextClassLoader()`，它返回特定线程的 Context ClassLoader。 Context ClassLoader 是线程的创建者在加载资源和类时提供的。如果未设置该值，则默认为父线程的 Context ClassLoader。

```java
@Test
public void testSystemClassLoader() {
  System.out.println(ClassLoader.getSystemClassLoader());

  ClassLoader classLoader = ClassloaderTest.class.getClassLoader();
  System.out.println(classLoader);
  System.out.println(classLoader.getParent());
  System.out.println(classLoader.getParent().getParent());

  classLoader = Thread.currentThread().getContextClassLoader();
  System.out.println(classLoader);
  System.out.println(classLoader.getParent());
  System.out.println(classLoader.getParent().getParent());
}
```

输出结果为：

```bash
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$ExtClassLoader@cc34f4d
null
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$ExtClassLoader@cc34f4d
null
```



### Java 动态代理

JDK动态代理是Java中的一种代理模式实现方式，它允许在运行时动态创建代理类和代理对象，以实现对目标对象的间接访问和控制。JDK动态代理是通过反射机制来实现的。

要使用JDK动态代理，需要以下步骤：

1. 创建一个实现了`InvocationHandler`接口的代理处理器类，该接口中只有一个方法`invoke`，用于处理方法调用。
2. 使用`Proxy`类的`newProxyInstance`方法创建代理对象。

```java
public class MyInvocationHandler implements InvocationHandler {
    private Object origin;
 
    public MyInvocationHandler(Object origin) {
        this.origin = origin;
    }
 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invoke start");
        Object result = method.invoke(origin, args);
        System.out.println("invoke end");
        return result;
    }
}
 
 
public class JdkProxyTest {
    public static void main(String[] args) {
        UserService proxy = (UserService) Proxy.newProxyInstance(JdkProxyTest.class.getClassLoader(),
                new Class[]{UserService.class}, new MyInvocationHandler(new UserServiceImpl()));
        proxy.doSomething();
    }
}
```

需要注意的是，JDK动态代理只能对实现了接口的类进行代理，无法对类的直接实例进行代理。如果要代理一个类而不是接口，可以考虑使用其他的代理实现方式，如CGLIB动态代理。

### Java 反射

### 字节码框架

#### CGLIB 代理

CGLIB（Code Generation Library）是一个强大的基于字节码生成的代理库，它可以在运行时动态生成目标类的子类作为代理类，实现对目标对象的代理访问和控制。与JDK动态代理不同，CGLIB代理可以代理没有实现任何接口的类。

要使用CGLIB代理，需要以下步骤：

1. 添加CGLIB库的依赖。可以通过Maven或Gradle等构建工具将CGLIB添加到项目中。
2. 创建一个类作为目标对象。
3. 创建一个实现了`MethodInterceptor`接口的代理拦截器类，该接口中只有一个方法`intercept`，用于处理方法调用。
4. 使用CGLIB库生成代理对象。

```java
public class CglibInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("intercept start");
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("intercept end");
        return result;
    }
}
 
public class CglibProxyTest {
	public static void main(String[] args) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(UserServiceImpl.class);
		enhancer.setCallback(new CglibInterceptor());
		UserServiceImpl proxy = (UserServiceImpl) enhancer.create();
		proxy.doSth();
	}
}
```

需要注意的是，CGLIB代理通过生成目标类的子类来实现代理，因此目标类不能是final类，并且无法代理final方法。此外，CGLIB代理会对目标类的所有非final方法进行代理，包括继承自父类的方法。

>除了CGLIB代理，还有其他几个常用的代理库可供选择，其中包括：
>
>1. JDK动态代理：JDK自带的动态代理是Java标准库的一部分。它通过反射机制在运行时动态生成代理类和代理对象，只能代理接口，无法代理类。使用JDK动态代理无需引入额外的库，但受限于只能代理接口的限制。
>2. Byte Buddy：Byte Buddy是一个简单而灵活的字节码生成和操作库，用于在运行时创建代理类和修改现有类的字节码。它支持生成基于接口的代理以及基于继承的代理。Byte Buddy提供了易于使用的API，并且性能较高。
>3. Javassist：Javassist是一个用于在运行时编辑字节码的库。它提供了一组简单而强大的API，用于创建和修改类、接口和方法。Javassist可以用于生成代理类，并且具有良好的性能。
>4. ASM（全称：Java bytecode manipulation framework）：ASM是一个强大的字节码处理库，它提供了直接操作字节码的细粒度控制。ASM可以用于生成代理类，但它的使用相对较复杂，需要手动编写字节码，适用于对字节码操作有高度要求的场景。
>
>这些代理库各有特点，可以根据具体需求选择合适的库。如果只需要简单的接口代理，JDK动态代理就足够了。如果需要对类进行代理或对字节码进行更精细的操作，可以考虑使用CGLIB、Byte Buddy、Javassist或ASM。

## Spring AOP的前世今生

目前 Spring AOP 一共有三种配置方式，Spring 做到了很好地向下兼容，所以可以放心使用。

- Spring 1.2 **基于接口的配置**：最早的 Spring AOP 是完全基于几个接口的
- Spring 2.0 **schema-based 配置**：Spring 2.0 以后使用 XML 的方式来配置，使用 命名空间 `<aop />`
- Spring 2.0 **@AspectJ 配置**：使用注解的方式来配置，这种方式感觉是最方便的，还有，这里虽然叫做 `@AspectJ`，但是这个和 AspectJ 其实没啥关系。

 

要说明的是，这里介绍的 Spring AOP 是纯的 Spring 代码，和 AspectJ 没什么关系，但是 Spring 延用了 AspectJ 中的概念，包括使用了 AspectJ 提供的 jar 包中的注解，但是不依赖于其实现功能。

> 如 @Aspect、@Pointcut、@Before、@After 等注解都是来自于 AspectJ，但是功能的实现是纯 Spring AOP 自己实现的。

## AOP 概述

1. AOP（Aspect Orient Programming）：面向切面编程；

2. 用途：用于系统中的横切关注点，比如日志管理，事务管理；

3. 实现：利用代理模式，通过代理对象对被代理的对象增加功能。

   所以，关键在于AOP框架自动创建AOP代理对象，代理模式分为静态代理和动态代理；

4. 框架： AspectJ使用静态代理，编译时增强，在编译期生成代理对象；

   SpringAOP使用动态代理，运行时增强，在运行时，动态生成代理对象；

## 概念

1. 切面（Aspect）：切面是一个模块化的单元，用于横向切割应用程序的关注点。它包含了一组通知（advice）和切点（pointcut）。通知定义了在目标方法执行前、执行后或抛出异常时要执行的逻辑，而切点定义了哪些方法会被拦截。

2. 连接点（Join Point）：连接点是在应用程序执行过程中能够插入切面的点。在Spring AOP 1.0中，连接点通常是方法调用。

3. 通知（Advice）：通知是切面在特定的切点处执行的代码。在Spring AOP 1.0中，通知通常是一个实现了`org.aopalliance.intercept.MethodInterceptor`接口的类，它拦截目标方法的调用，并在方法执行前后执行一些额外的逻辑。

4. 切点（Point Cut）：切点定义了哪些方法会被切面拦截。在Spring AOP 1.0中，切点使用表达式来描述要拦截的方法，通常使用AspectJ切点表达式语言。

5. 引入（Introduction）：引入允许向现有的类添加新的接口和实现，以便在现有类上使用新的功能。在Spring AOP 1.0中，引入是通过特殊的通知类型实现的。

6. 织入（Weaving）：织入是将切面应用到目标对象并创建代理对象的过程。在Spring AOP 1.0中，织入是在运行时通过动态生成代理对象实现的。

​	Spring采用动态代理织入，而AspectJ采用编译期织入和类装载期织入。

## Pointcut

spring利用Pointcut表示切点，其中ClassFilter用来确定切点应用到哪些目标类上，而MethodMatcher用来确定切点是否可以应用到目标类的方法上，通过分开成2个方法就可以2个单独实现并任意组合，非常棒的设计，代码如下

```java
public interface Pointcut {
  Pointcut TRUE = new Pointcut() {
		public ClassFilter getClassFilter() {
			return ClassFilter.TRUE;
		}

		public MethodMatcher getMethodMatcher() {
			return MethodMatcher.TRUE;
		}

		public String toString() {
			return "Pointcut.TRUE";
		}
	};
  ClassFilter getClassFilter();
  MethodMatcher getMethodMatcher();
}
```

### ClassFilter

ClassFilter是用来确定哪些目标类可以被增强的，通常情况下这个类的实现没有MethodMatcher的实现重要，整个spring 1.0框架内没有特别有价值的ClassFilter实现。ClassFilter的实现通常直接返回true就可以了，表示任何目标类都适用。

```
public interface ClassFilter {
	boolean matches(Class clazz);
}
```

#### TRUE

这个是ClassFilter接口内部的一个常量，是一种匿名内部类的实现方法，表示任意类型都可以是要进行增强的目标类

```
ClassFilter TRUE = new ClassFilter() {
  public boolean matches(Class clazz) {
    return true;
  }
};
```

#### RootClassFilter

这个ClassFilter实现就检查目标类是否可以赋值给指定的类型，代码很简单

```
public class RootClassFilter implements ClassFilter {
	
	private Class clazz;
	

	public RootClassFilter(Class clazz) {
		this.clazz = clazz;
	}

	public boolean matches(Class candidate) {
		return clazz.isAssignableFrom(candidate);
	}

}
```

#### UnionClassFilter

spring提供了一些ClassFilter的实现类似集合的操作，比如并集与交集，这一点在 ClassFilters 抽象类里面有体现。



先看看并集操作的类UnionClassFilter，代码如下

```
private static class UnionClassFilter implements ClassFilter {

  private ClassFilter[] filters;

  public UnionClassFilter(ClassFilter[] filters) {
    this.filters = filters;
  }

  public boolean matches(Class clazz) {
    for (int i = 0; i < filters.length; i++) {
      if (filters[i].matches(clazz)) {
        return true;
      }
    }
    return false;
  }
}
```

#### IntersectionClassFilter

而IntersectionClassFilter表示交集操作，就是目标类必须满足所有的ClassFilter实现，它也是ClassFilters的静态内部类

```
private static class IntersectionClassFilter implements ClassFilter {

  private ClassFilter[] filters;

  public IntersectionClassFilter(ClassFilter[] filters) {
    this.filters = filters;
  }

  public boolean matches(Class clazz) {
    for (int i = 0; i < filters.length; i++) {
      if (!filters[i].matches(clazz)) {
        return false;
      }
    }
    return true;
  }
}
```

#### ClassFilters

而ClassFilters就提供了2个工具方法来使用这2个内部的类

```
public abstract class ClassFilters {

  public static ClassFilter union(ClassFilter a, ClassFilter b) {
    return new UnionClassFilter(new ClassFilter[] { a, b } );
  }

  public static ClassFilter intersection(ClassFilter a, ClassFilter b) {
    return new IntersectionClassFilter(new ClassFilter[] { a, b } );
  }
}
```

利用这种设计你就可以写出下面的代码，进行任意的组合使用了

```
ClassFilter cf1;
ClassFilter cf2;
ClassFilter cf2;
ClassFilter cf4 = ClassFilters.intersection(ClassFilters.union(cf1,cf2),cf3);
```

### MethodMatcher

MethodMatcher分为静态匹配评估与动态匹配评估，静态评估就是2参数的matches方法的任务，不考虑运行时方法的实际参数值，动态评估也就是在isRuntime返回true时，就需要考虑方法的参数值，以便基于不同的值做出不同的增强处理

动态评估也就是3参数的matches方法只有在isRuntime与2参数的matches都返回true时才会得到调用，而且3参数的matches方法是运行在Advice之前，也就是说Advice紧跟着这个3参数的matches方法执行

大多数的实现是静态的，也就是说isRuntime返回false并且3参数的matches方法永远得不到调用，而且spring官方也推荐尽可能的让pointcut是静态的，以便可以缓存评估的结果，基于此，后面章节就不再分析动态MethodMatcher以及动态Pointcut

> If possible, try to make pointcuts static, allowing the AOP framework to cache the results of pointcut evaluation when an AOP proxy is created.

```
public interface MethodMatcher {

  boolean matches(Method m, Class targetClass);

  boolean isRuntime();

  boolean matches(Method m, Class targetClass, Object[] args);
}
```

#### StaticMethodMatcher 与DynamicMethodMatcher

为了实现的方便，spring提供了静态实现的一个父类与动态实现的父类，静态的实现把isRuntime的方法用final修饰了，表示子类不能修改这个方法了。

```
public abstract class StaticMethodMatcher implements MethodMatcher {

	public final boolean isRuntime() {
		return false;
	}

	public final boolean matches(Method m, Class targetClass, Object[] args) {
		// Should never be invoked because isRuntime() returns false
		throw new UnsupportedOperationException("Illegal MethodMatcher usage");
	}

}
```

下面的代码注意isRuntime修饰为final表示子类不能改写这个方法的实现，但2参数的matches是可以被子类改写的

```
public abstract class DynamicMethodMatcher implements MethodMatcher {

	public final boolean isRuntime() {
		return true;
	}

	public boolean matches(Method m, Class targetClass) {
		return true;
	}

}
```

#### True

在MethodMatcher接口中有声明一个TRUE常量，用于表示任何方法都匹配

```
MethodMatcher TRUE = new MethodMatcher() {

  public boolean isRuntime() {
    return false;
  }

  public boolean matches(Method m, Class targetClass) {
    return true;
  }

  public boolean matches(Method m, Class targetClass, Object[] args) {
    // should never be invoked as isRuntime returns false
    throw new UnsupportedOperationException();
  }
};
```

#### UnionMethodMatcher

```
private static class UnionMethodMatcher implements MethodMatcher {

  private MethodMatcher a;
  private MethodMatcher b;

  public UnionMethodMatcher(MethodMatcher a, MethodMatcher b) {
    this.a = a;
    this.b = b;
  }

  public boolean matches(Method m, Class targetClass) {
    return a.matches(m, targetClass) || b.matches(m, targetClass);
  }

  public boolean isRuntime() {
    return a.isRuntime() || b.isRuntime();
  }

  public boolean matches(Method m, Class targetClass, Object[] args) {
    return a.matches(m, targetClass, args) || b.matches(m, targetClass, args);
  }
}
```

#### IntersectionMethodMatcher

```
private static class IntersectionMethodMatcher implements MethodMatcher {

  private MethodMatcher a;
  private MethodMatcher b;

  public IntersectionMethodMatcher(MethodMatcher a, MethodMatcher b) {
    this.a = a;
    this.b = b;
  }

  public boolean matches(Method m, Class targetClass) {
    return a.matches(m, targetClass) && b.matches(m, targetClass);
  }

  public boolean isRuntime() {
    return a.isRuntime() || b.isRuntime();
  }

  public boolean matches(Method m, Class targetClass, Object[] args) {
    boolean aMatches = a.isRuntime() ? a.matches(m, targetClass, args) : a.matches(m, targetClass);
    boolean bMatches = b.isRuntime() ? b.matches(m, targetClass, args) : b.matches(m, targetClass);
    return  aMatches && bMatches;
  }
}
```

#### MethodMatchers

```
public abstract class MethodMatchers {

  public static MethodMatcher union(MethodMatcher a, MethodMatcher b) {
    return new UnionMethodMatcher(a, b);
  }

  public static MethodMatcher intersection(MethodMatcher a, MethodMatcher b) {
    return new IntersectionMethodMatcher(a, b);
  }
}
```



## 参考

- [Spring中AOP的配置从1.0到5.0的演进](https://blog.csdn.net/chijunmei7041/article/details/100854818)
- [Spring1.x 2.x 3.x AOP的区别](https://blog.csdn.net/freesky_zh/article/details/84703600)

 
