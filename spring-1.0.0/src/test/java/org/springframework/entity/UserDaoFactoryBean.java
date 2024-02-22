package org.springframework.entity;

import org.springframework.beans.factory.FactoryBean;

public class UserDaoFactoryBean implements FactoryBean {
	public UserDao getObject() throws Exception {
		System.out.println("FactoryBean getObject....");

		return new UserDaoImpl();
	}

	public Class<?> getObjectType() {
		return UserDao.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
