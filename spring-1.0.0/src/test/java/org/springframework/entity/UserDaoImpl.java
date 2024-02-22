package org.springframework.entity;

public class UserDaoImpl extends BaseDao implements UserDao {

	public UserDaoImpl() {
		System.out.println("---UserDaoImpl 默认构造函数---");
	}

	public void insert() {
		System.out.println("insert into db in userDaoImpl---");
	}
}
