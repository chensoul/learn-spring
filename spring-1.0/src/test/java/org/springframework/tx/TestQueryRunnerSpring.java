package org.springframework.tx;

import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;
import org.springframework.jdbc.MyDataSourceUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TestQueryRunnerSpring {
	@Test
	public void testQueryRunner() throws Exception {
		DataSource dataSource = MyDataSourceUtils.dataSource();

		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transaction = txManager.getTransaction(transactionDefinition);

		QueryRunner queryRunner = new QueryRunner(dataSource, true);
		try {
			String sql = "delete from employee where id = ?";
			queryRunner.update(sql, new Object[]{32});

			String sql2 = "insert into dept( deptname) values (?)";
			Object object = queryRunner.update(sql2, new Object[]{"jdbc12345678"});
			System.out.println("object = " + object);

			txManager.commit(transaction);
		} catch (Exception e) {
			System.out.println("出现异常了-------" + e.getMessage());
			// e.printStackTrace();
			txManager.rollback(transaction);
		}

	}


	@Test
	public void testQueryRunnerWithSpring() {
		DataSource dataSource = MyDataSourceUtils.dataSource();

		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transaction = txManager.getTransaction(transactionDefinition);

		QueryRunnerSpring queryRunnerSpring = new QueryRunnerSpring(dataSource, true);
		try {
			String sql = "delete from employee where id = ?";
			queryRunnerSpring.update(sql, new Object[]{34});

			String sql2 = "insert into dept( deptname) values (?)";
			Object object = queryRunnerSpring.update(sql2, new Object[]{"jdbc12345678"});
			System.out.println("object = " + object);

			txManager.commit(transaction);
		} catch (Exception e) {
			System.out.println("出现异常了-------" + e.getMessage());
			// e.printStackTrace();
			txManager.rollback(transaction);
		}
	}
}
