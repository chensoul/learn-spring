package org.springframework.tx;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.MyDataSourceUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeEditor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TestTransaction {
	@Test
	public void testInsertWithoutTx() {
		DataSource dataSource = MyDataSourceUtils.dataSource();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from employee where id = ?";
		jdbcTemplate.update(sql, new Object[]{35});

		String sql2 = "insert into dept( deptname) values (?)";
		Object object = jdbcTemplate.update(sql2, new Object[]{"jdbc12345678"});
		System.out.println("object = " + object);

	}


	/**
	 * 演示非事务模式下运行原理
	 * 可以让sql语句出错,演示一下回滚处理逻辑
	 */
	@Test
	public void testQueryWithTx() {
		DataSource dataSource = MyDataSourceUtils.dataSource();

		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		transactionDefinition.setReadOnly(true);
		transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_NEVER);
		TransactionStatus transaction = txManager.getTransaction(transactionDefinition);
		// 注意实例化JdbcTemplate对象时异常转换器从DataSource中获取连接以得到错误码对ConnectionSynchronization加入事务同步管理器的干扰
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "select username from employee where id = ?";

			String username = (String) jdbcTemplate.queryForObject(sql, new Object[]{2}, String.class);
			System.out.println("----------username = " + username);

			// 注释掉下面的代码,数据也可以查询出来,因为是运行在非事务环境下
			txManager.commit(transaction);
		} catch (Exception e) {
			System.out.println("出现异常了-------" + e.getMessage());
			// e.printStackTrace();
			txManager.rollback(transaction);
		}
	}

	/**
	 * 这个方法用来研究事务的回滚与提交的源码流程
	 * 改变插入数据的长度超过10就会发生回滚
	 */
	@Test
	public void testSingleCUDOperationWithTx() {
		DataSource dataSource = MyDataSourceUtils.dataSource();

		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transaction = txManager.getTransaction(transactionDefinition);

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {

			String sql = "insert into dept( deptname) values (?)";
			Object object = jdbcTemplate.update(sql, new Object[]{"tx1231111111"});
			System.out.println("object = " + object);
			// 没有下面的代码是不会往数据库中插入一条记录的
			txManager.commit(transaction);
		} catch (Exception e) {
			System.out.println("出现异常了-------" + e.getMessage());
			// e.printStackTrace();
			txManager.rollback(transaction);
		}
	}

	@Test
	public void testMultiOperationWithTx() {
		DataSource dataSource = MyDataSourceUtils.dataSource();

		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transaction = txManager.getTransaction(transactionDefinition);

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "delete from employee where id = ?";
			// jdbcTemplate的update方法本来是可以真正操作数据库的,为什么在事务环境下就不操作数据库了?
			jdbcTemplate.update(sql, new Object[]{36});

			String sql2 = "insert into dept( deptname) values (?)";
			Object object = jdbcTemplate.update(sql2, new Object[]{"jdbc12345678"});
			System.out.println("object = " + object);

			// 上面的insert操作数据过长是会抛异常的,抛出异常后下面的代码就不执行了,进入catch代码块
			txManager.commit(transaction);
		} catch (Exception e) {
			System.out.println("出现异常了-------" + e.getMessage());
			// e.printStackTrace();
			txManager.rollback(transaction);
		}
	}

	/**
	 * 测试已经存在外围事务的情况下事务的运行原理,对应事务管理器的getTransation方法中的isExistingTransaction(transaction) if块
	 */
	@Test
	public void testExistingTransaction() {
		DataSource dataSource = MyDataSourceUtils.dataSource();
		DeptServiceImplWithTx deptServiceImplWithTx = new DeptServiceImplWithTx(dataSource);
		EmployeeServiceImplWithTx employeeServiceImplWithTx = new EmployeeServiceImplWithTx(dataSource, deptServiceImplWithTx);

		employeeServiceImplWithTx.delete();
	}

	@Test
	public void testTransactionTemplate() {
		DataSource dataSource = MyDataSourceUtils.dataSource();

		DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);
		TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);

		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "delete from employee where id = ?";
				int rows = jdbcTemplate.update(sql, new Object[]{36});
				return rows;
			}
		});
	}

	@Test
	public void testTransactionTemplateWithXml() {
		ClassPathResource resource = new ClassPathResource("tx.xml");
		XmlBeanFactory factory = new XmlBeanFactory(resource);
		DeptServiceImpl deptService = (DeptServiceImpl) factory.getBean("deptService");
		deptService.insert();
	}
	//====================================TransactionAttributeSource===================================================

	@Test
	public void testMatchAlwaysTransactionAttributeSource() {
		ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("tx.xml");
		TransactionAttributeSource source = (TransactionAttributeSource) factory.getBean("txAttr");
		RuleBasedTransactionAttribute transactionAttribute = (RuleBasedTransactionAttribute) source.getTransactionAttribute(null, null);
		System.out.println("transactionAttribute = " + transactionAttribute);
	}

	@Test
	public void testNameMatchTransactionAttributeSource() throws Exception {
		ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("tx.xml");
		TransactionAttributeSource source = (TransactionAttributeSource) factory.getBean("txAttr2");
		Class<DeptServiceImplDeclarative> aClass = DeptServiceImplDeclarative.class;
		Method method = aClass.getDeclaredMethod("insert");
		RuleBasedTransactionAttribute transactionAttribute = (RuleBasedTransactionAttribute) source.getTransactionAttribute(method, null);
		System.out.println("transactionAttribute = " + transactionAttribute);
	}

	/**
	 * MethodMapTransactionAttributeSource不能直接在xml中进行配置,
	 *
	 * @throws Exception
	 */
	@Test
	public void testMethodMapTransactionAttributeSource() throws Exception {

		TransactionAttributeEditor editor = new TransactionAttributeEditor();
		editor.setAsText("PROPAGATION_REQUIRED,readOnly,-DataAccessException");
		Map<String, TransactionAttribute> map = new HashMap<>();
		map.put("tx.DeptServiceImplDeclarative.insert*", (TransactionAttribute) editor.getValue());
		MethodMapTransactionAttributeSource source = new MethodMapTransactionAttributeSource();
		source.setMethodMap(map);
		System.out.println("===============================");
		Class<DeptServiceImplDeclarative> aClass = DeptServiceImplDeclarative.class;
		Method method = aClass.getDeclaredMethod("insert2");
		TransactionAttribute transactionAttribute = source.getTransactionAttribute(method, null);
		System.out.println(transactionAttribute);
	}

	@Test
	public void testTransactionInterceptor() throws Exception {
		ClassPathResource resource = new ClassPathResource("tx.xml");
		XmlBeanFactory factory = new XmlBeanFactory(resource);
		DeptServiceImplDeclarative deptService = (DeptServiceImplDeclarative) factory.getBean("deptServiceTxProxy");
		deptService.insert();
		// deptService.getById();
	}

	@Test
	public void testTransactionProxyFactoryBean() throws Exception {
		ClassPathResource resource = new ClassPathResource("tx.xml");
		XmlBeanFactory factory = new XmlBeanFactory(resource);
		DeptServiceImplDeclarative deptService = (DeptServiceImplDeclarative) factory.getBean("deptServiceWithDeclarative");
		deptService.insert();
		// deptService.getById();
	}

	@Test
	public void testBeanNameAutoProxyCreator() throws Exception {
		ClassPathXmlApplicationContext factory = new ClassPathXmlApplicationContext("tx.xml");
		DeptServiceImplDeclarative deptService = (DeptServiceImplDeclarative) factory.getBean("deptServiceTarget");
		deptService.insert();
	}
}
