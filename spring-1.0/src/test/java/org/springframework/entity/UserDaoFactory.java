package org.springframework.entity;

public class UserDaoFactory {
	public static UserDao getUserDao() {
		System.out.println("factory setup....");
		return new UserDaoImpl();
	}
}
