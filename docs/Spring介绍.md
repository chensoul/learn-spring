## Spring发展历史

![spring发展历史](./assets/spring发展历史.png)

### **Spring 1.x**

大概在2004年3月24日这一天，[Spring Framework 1.0 final正式发版](https://spring.io/blog/2004/03/24/spring-framework-1-0-final-released)。源码下载地址：https://sourceforge.net/projects/springframework/files/



引用依赖如下：

```xml
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring</artifactId>
	<version>1.0</version>
</dependency>
```

Spring 1.0当时只包含一个完整的项目，他把所有的功能都集中在一个项目中，其中包含了核心的Ioc、AOP，同时也包含了其他的诸多功能，例如：JDBC、Mail、ORM、事务、定时任务、Spring MVC等。

### **Spring 2.x**

Spring 2.x增加对注解的支持，支持了基于注解的配置。

引用依赖如下：

```xml
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring</artifactId>
	<version>2.0</version>
</dependency>
```

### **Spring 3.x**

Spring在GitHub托管代码，最早的版本 3.0.x：[https://github.com/spring-projects/spring-framework](https://github.com/spring-projects/spring-framework)

Spring 3.x支持了基于Java类的配置。

### **Spring 4.x**

**Spring 4.x新特性**

- 全面支持Java 8
- 核心容器增强
- 支持了基于Groovy DSL的配置
- Web 增强
- WebSocket 支持

### **Spring 5.x**

**Spring 5.x新特性**

- JDK 更新

## Spring架构图

![spring系统架构图](./assets/spring系统架构图.png)

## Spring学习路线

- IOC
- AOP
- 事务
- 整合
- 全家桶
  - Spring WebMVC
  - Spring Boot
  - Spring Cloud

