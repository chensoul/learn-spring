package org.springframework.context;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.entity.UserDao;
import org.springframework.entity.UserServiceImpl;

public class App {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("user.xml");
		UserDao userDao = (UserDao) ctx.getBean("userDao");
		userDao.insert();

//		UserDao userDao2 = (UserDao) ctx.getBean("userDao2");
//		userDao2.insert();

		UserDao userDao4 = (UserDao) ctx.getBean("userDao4");
		userDao4.insert();

		UserServiceImpl userService = (UserServiceImpl) ctx.getBean("userService");
		System.out.println(userService);
		userService.insert();

		userService = (UserServiceImpl) ctx.getBean("userService1");
		System.out.println(userService);
		userService.insert();

		//关闭容器，执行销毁的方法
		ctx.close();
	}
}
