/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.orm.ibatis.support;

import com.ibatis.db.sqlmap.SqlMap;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapTemplate;

/**
 * Convenient super class for iBATIS SqlMap data access objects.
 * Requires a DataSource to be set, providing a SqlMapTemplate
 * based on it to subclasses.
 *
 * @author Juergen Hoeller
 * @see SqlMapTemplate
 * @since 29.11.2003
 */
public class SqlMapDaoSupport {

	protected final Log logger = LogFactory.getLog(getClass());

	private SqlMapTemplate sqlMapTemplate = new SqlMapTemplate();

	/**
	 * Return the JDBC DataSource used by this DAO.
	 */
	protected final DataSource getDataSource() {
		return sqlMapTemplate.getDataSource();
	}

	/**
	 * Set the JDBC DataSource to be used by this DAO.
	 */
	public final void setDataSource(DataSource dataSource) {
		this.sqlMapTemplate.setDataSource(dataSource);
	}

	/**
	 * Return the iBATIS Database Layer SqlMap that this template works with.
	 */
	protected final SqlMap getSqlMap() {
		return this.sqlMapTemplate.getSqlMap();
	}

	/**
	 * Set the iBATIS Database Layer SqlMap to work with.
	 */
	public final void setSqlMap(SqlMap sqlMap) {
		this.sqlMapTemplate.setSqlMap(sqlMap);
	}

	/**
	 * Return the JdbcTemplate for this DAO,
	 * pre-initialized with the DataSource or set explicitly.
	 */
	protected final SqlMapTemplate getSqlMapTemplate() {
		return sqlMapTemplate;
	}

	/**
	 * Set the JdbcTemplate for this DAO explicitly,
	 * as an alternative to specifying a DataSource.
	 */
	public final void setSqlMapTemplate(SqlMapTemplate sqlMapTemplate) {
		this.sqlMapTemplate = sqlMapTemplate;
	}

	public final void afterPropertiesSet() throws Exception {
		this.sqlMapTemplate.afterPropertiesSet();
		initDao();
	}

	/**
	 * Subclasses can override this for custom initialization behavior.
	 * Gets called after population of this instance's bean properties.
	 *
	 * @throws Exception if initialization fails
	 */
	protected void initDao() throws Exception {
	}

}
