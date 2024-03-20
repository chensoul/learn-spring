package org.springframework.tx;

public class DeptServiceImplDeclarative {
	private DeptDaoImpl deptDao;

	public DeptDaoImpl getDeptDao() {
		return deptDao;
	}

	public void setDeptDao(DeptDaoImpl deptDao) {
		this.deptDao = deptDao;
	}

	public void insert() {
		deptDao.insert();
	}

	public void insert2() {
		System.out.println("---insert2 -----");
	}

	public void getById() {
		System.out.println("---getById -----");
	}
}
