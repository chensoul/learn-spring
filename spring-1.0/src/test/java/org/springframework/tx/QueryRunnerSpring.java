
package org.springframework.tx;


import org.apache.commons.dbutils.QueryRunner;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class QueryRunnerSpring extends QueryRunner {

  public QueryRunnerSpring(DataSource ds, boolean pmdKnownBroken) {
    super(ds, pmdKnownBroken);
  }

  @Override
  protected Connection prepareConnection() throws SQLException {
    Connection connection = DataSourceUtils.getConnection(this.getDataSource());
    return connection;
  }

  @Override
  protected void close(Connection conn) throws SQLException {
    DataSourceUtils.closeConnectionIfNecessary(conn, this.getDataSource());
  }
}
