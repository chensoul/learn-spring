package org.springframework.tx;

import org.springframework.jdbc.core.JdbcTemplate;

public class DeptDaoImpl {
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int insert() {
		String sql = "insert into dept( deptname) values (?)";
		//Object object = jdbcTemplate.update(sql, new Object[]{"jdbc123"});
		int rows = jdbcTemplate.update(sql, new Object[]{"jdbc12345678"});
		return rows;
	}
}
