package org.springframework.tx;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

public class DeptServiceImplWithTx {
  private DataSource dataSource;

  public DeptServiceImplWithTx(DataSource dataSource) {
    this.dataSource = dataSource;

  }

  public void insert() {
    DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
    int propagationMandatory = TransactionDefinition.PROPAGATION_MANDATORY;
    int propagationSupports = TransactionDefinition.PROPAGATION_SUPPORTS;

    int propagationNotSupported = TransactionDefinition.PROPAGATION_NOT_SUPPORTED;

    int propagationRequiresNew = TransactionDefinition.PROPAGATION_REQUIRES_NEW;

    //    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition(propagationRequiresNew);
    TransactionStatus transaction = txManager.getTransaction(transactionDefinition);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    try {

      String sql = "insert into dept( deptname) values (?)";
      //Object object = jdbcTemplate.update(sql, new Object[]{"jdbc123"});
      Object object = jdbcTemplate.update(sql, new Object[]{"jdbc12345678"});

      txManager.commit(transaction);
    } catch (Exception e) {
      System.out.println("出现异常了-------" + e.getMessage());
      txManager.rollback(transaction);
    }
  }
}
