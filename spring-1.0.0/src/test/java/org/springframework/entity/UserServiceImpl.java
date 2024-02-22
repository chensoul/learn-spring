package org.springframework.entity;

public class UserServiceImpl {

	private Object dao;

	public UserServiceImpl() {
		System.out.println("---默认构造函数---");
	}

	public UserServiceImpl(BaseDao dao) {
		System.out.println("---构造函数注入BaseDao---");
		this.dao = dao;
	}

	public UserServiceImpl(UserDao dao) {
		System.out.println("---构造函数注入UserDao---");
		this.dao = dao;
	}

	public UserDao getDao() {
		return (UserDao) dao;
	}

	public void setDao(UserDao dao) {
		System.out.println("---set注入UserDao---");
		this.dao = dao;
	}

	public void insert() {
		System.out.println("insert in UserServiceImpl---");
		if (dao != null) {
			((UserDao) dao).insert();
		}
	}

	//表示bean初始化对应的操作
	public void init() {
		System.out.println("init...");
	}

	//表示bean销毁前对应的操作
	public void destroy() {
		System.out.println("destroy...");
	}
}
