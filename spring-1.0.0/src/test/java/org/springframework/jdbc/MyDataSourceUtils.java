package org.springframework.jdbc;


import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;

public class MyDataSourceUtils {
	public static DataSource dataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUsername("root");
		druidDataSource.setPassword("123456");
		druidDataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true");
		return druidDataSource;
	}
}
