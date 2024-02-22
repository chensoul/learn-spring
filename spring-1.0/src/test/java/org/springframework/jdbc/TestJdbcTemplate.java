package org.springframework.jdbc;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestJdbcTemplate {

  @Test
  public void testGetScalarValueStatic() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(MyDataSourceUtils.dataSource());
    String sql = "select deptname from dept where id = 46";
    Object object = jdbcTemplate.queryForObject(sql, String.class);
    System.out.println("object = " + object);
  }

  @Test
  public void testGetScalarValueWithParam() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(MyDataSourceUtils.dataSource());
    String sql = "select deptname from dept where id = ?";
    Object object = jdbcTemplate.queryForObject(sql, new Object[]{46}, String.class);
    System.out.println("object = " + object);
  }

  @Test
  public void testInsertWithParam() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(MyDataSourceUtils.dataSource());
    String sql = "insert into dept( deptname) values (?)";
    Object object = jdbcTemplate.update(sql, new Object[]{"org/springframework/jdbc"});
    System.out.println("object = " + object);
  }

}
