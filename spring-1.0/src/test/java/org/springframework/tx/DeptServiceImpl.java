package org.springframework.tx;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class DeptServiceImpl {
	private DeptDaoImpl deptDao;
	private TransactionTemplate transactionTemplate;

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public DeptDaoImpl getDeptDao() {
		return deptDao;
	}

	public void setDeptDao(DeptDaoImpl deptDao) {
		this.deptDao = deptDao;
	}

	public void insert() {
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				return deptDao.insert();
			}
		});
	}

}
