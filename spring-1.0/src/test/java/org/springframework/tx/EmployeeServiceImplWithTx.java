package org.springframework.tx;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class EmployeeServiceImplWithTx {
	private DataSource dataSource;
	private DeptServiceImplWithTx deptServiceImplWithTx;

	public EmployeeServiceImplWithTx(DataSource dataSource, DeptServiceImplWithTx deptServiceImplWithTx) {
		this.dataSource = dataSource;
		this.deptServiceImplWithTx = deptServiceImplWithTx;
	}

	public void delete() {
		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transaction = txManager.getTransaction(transactionDefinition);

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "delete from employee where id = ?";
			jdbcTemplate.update(sql, new Object[]{36});
			deptServiceImplWithTx.insert();
			txManager.commit(transaction);
		} catch (Exception e) {
			System.out.println("出现异常了-------" + e.getMessage());
			txManager.rollback(transaction);
		}
	}
}
