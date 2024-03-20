package org.springframework.tx;

import org.springframework.jdbc.core.JdbcTemplate;

public class EmployeeDaoImpl {
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int delete() {
		String sql = "delete from employee where id = ?";
		int rows = jdbcTemplate.update(sql, new Object[]{36});
		return rows;
	}
}
