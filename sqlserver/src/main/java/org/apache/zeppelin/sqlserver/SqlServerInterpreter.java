/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.zeppelin.sqlserver;

import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterPropertyBuilder;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.scheduler.Scheduler;
import org.apache.zeppelin.scheduler.SchedulerFactory;

import java.sql.*;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL Server interpreter v2 for Zeppelin.
 */
public class SqlServerInterpreter extends Interpreter
{
  private static final String VERSION = "2.0.4.1";

  private static final char NEWLINE = '\n';
  private static final char TAB = '\t';
  private static final String TABLE_MAGIC_TAG = "%table ";
  private static final String NOTEBOOK_CONNECTION_STYLE = "notebook";
  private static final String PARAGRAPH_CONNECTION_STYLE = "paragraph";

  private static final String DEFAULT_JDBC_URL = "jdbc:sqlserver://localhost:1433";
  private static final String DEFAULT_JDBC_USER_PASSWORD = "";
  private static final String DEFAULT_JDBC_USER_NAME = "zeppelin";
  private static final String DEFAULT_JDBC_DATABASE_NAME = "tempdb";
  private static final String DEFAULT_JDBC_DRIVER_NAME =
    "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  private static final String DEFAULT_MAX_RESULT = "1000";

  /*
    Notebook
      Single global connection.
      Connection is opened when notebook is opened and
      closed when notebook is closed

     Paragraph
      Each paragraph has its own connection.
      Connection is opened and closed at each execution
  */
  private static final String DEFAULT_CONNECTION_STYLE = NOTEBOOK_CONNECTION_STYLE;

  private static final String SQLSERVER_SERVER_URL = "sqlserver.url";
  private static final String SQLSERVER_SERVER_USER = "sqlserver.user";
  private static final String SQLSERVER_SERVER_PASSWORD = "sqlserver.password";
  private static final String SQLSERVER_SERVER_DATABASE_NAME = "sqlserver.database";
  private static final String SQLSERVER_SERVER_DRIVER_NAME = "sqlserver.driver.name";
  private static final String SQLSERVER_SERVER_MAX_RESULT = "sqlserver.max.result";
  private static final String SQLSERVER_SERVER_CONNECTION_STYLE = "sqlserver.connections";

  private Logger _logger = LoggerFactory.getLogger(SqlServerInterpreter.class);
  private int _maxRows = 1000;
  private Connection _jdbcGlobalConnection = null;
  private boolean _useNotebookConnection = true;

  static {
    Interpreter.register(
      "sql",
      "tsql",
      SqlServerInterpreter.class.getName(),
      new InterpreterPropertyBuilder()
        .add(SQLSERVER_SERVER_URL, DEFAULT_JDBC_URL, "JDBC URL for SQL Server.")
        .add(SQLSERVER_SERVER_USER, DEFAULT_JDBC_USER_NAME, "SQL Server user name")
        .add(SQLSERVER_SERVER_PASSWORD, DEFAULT_JDBC_USER_PASSWORD, "SQL Server user password")
        .add(SQLSERVER_SERVER_DATABASE_NAME, DEFAULT_JDBC_DATABASE_NAME, "SQL Server database")
        .add(SQLSERVER_SERVER_DRIVER_NAME, DEFAULT_JDBC_DRIVER_NAME, "JDBC Driver Name")
        .add(SQLSERVER_SERVER_MAX_RESULT, DEFAULT_MAX_RESULT,
          "Max number of SQL result to display.")
        .add(SQLSERVER_SERVER_CONNECTION_STYLE, DEFAULT_CONNECTION_STYLE,
          "Notebook or Paragraph connection style.")
        .build());
  }

  public SqlServerInterpreter(Properties property) {
    super(property);
  }

  private Connection openSQLServerConnection()
  {
    try {
      if (_jdbcGlobalConnection != null && _useNotebookConnection) {
        if (!_jdbcGlobalConnection.isClosed())
          return _jdbcGlobalConnection;
        else
          _logger.info("Notebook connection is closed.");
      }
    } catch (SQLException e)
    {
      _logger.error("Exception trapped while checking if connection is closed.", e);
    }

    _logger.debug("Opening SQL Server connection...");
    Connection jdbcConnection = null;

    try
    {
      String driverName = getProperty(SQLSERVER_SERVER_DRIVER_NAME);
      String url = getProperty(SQLSERVER_SERVER_URL);
      String user = getProperty(SQLSERVER_SERVER_USER);
      String password = getProperty(SQLSERVER_SERVER_PASSWORD);
      String database = getProperty(SQLSERVER_SERVER_DATABASE_NAME);

      _maxRows = Integer.valueOf(getProperty(SQLSERVER_SERVER_MAX_RESULT));

      Class.forName(driverName);

      url = url + ";databaseName=" + database;
      jdbcConnection = DriverManager.getConnection(url, user, password);

    } catch (ClassNotFoundException | SQLException e)
    {
      _logger.error("Cannot open connection.", e);
    }

    if (jdbcConnection != null)
    {
      _logger.debug("Connection opened successfully.");
    }

    if (_useNotebookConnection)
      _jdbcGlobalConnection = jdbcConnection;
    else
      _jdbcGlobalConnection = null;

    return jdbcConnection;
  }

  private void closeSQLServerConnection(Connection jdbcConnection)
  {
    closeSQLServerConnection(jdbcConnection, false);
  }

  private void closeSQLServerConnection(Connection jdbcConnection, boolean force)
  {
    if (_useNotebookConnection && !force) return;

    try
    {
      if (jdbcConnection != null)
        jdbcConnection.close();
    } catch (SQLException e)
    {
      _logger.error("Cannot close connection.", e);
    }
  }

  private String replaceTableSpecialChar(String input) {
    if (input == null)
      return "";

    return input.replace(TAB, ' ').replace(NEWLINE, ' ');
  }

  private InterpreterResult executeMetaCommand(String cmd)
  {
    _logger.debug("Meta Command: '" + cmd + "'");

    InterpreterResult.Code result = InterpreterResult.Code.SUCCESS;
    StringBuilder resultMessage = new StringBuilder();

    if (cmd.toLowerCase().trim().equals(":info"))
    {
      resultMessage
        .append(String.format("Using notebook connection: %1s", _useNotebookConnection))
        .append(NEWLINE);
    }

    if (resultMessage.length() == 0)
    {
      result = InterpreterResult.Code.ERROR;
      resultMessage.append("Meta-command not known.");
    }

    return new InterpreterResult(result, resultMessage.toString());
  }


  @Override
  public void open() {
    _logger.info(String.format("Starting T-SQL Interpreter v %1s", VERSION));

    String connectionStyle = getProperty(SQLSERVER_SERVER_CONNECTION_STYLE);
    _logger.info(String.format("Connection style: %1s", connectionStyle));
    _useNotebookConnection = !(connectionStyle.toLowerCase().equals(PARAGRAPH_CONNECTION_STYLE));

    Connection jdbcConnection = openSQLServerConnection();

    if (jdbcConnection != null) {
      _logger.debug("TODO: Load autocomplete.");
      closeSQLServerConnection(jdbcConnection);
    }
  }

  @Override
  public void close() {
    _logger.info("Releasing SQL Server Interpreter");

    closeSQLServerConnection(_jdbcGlobalConnection, true);
  }

  @Override
  public InterpreterResult interpret(String cmd, InterpreterContext contextInterpreter) {
    InterpreterResult.Code result;
    StringBuilder resultMessage = new StringBuilder();

    if (cmd.startsWith(":")) {
      return executeMetaCommand(cmd);
    }

    _logger.debug("T-SQL command: '" + cmd + "'");

    Connection jdbcConnection = openSQLServerConnection();
    if (jdbcConnection == null) {
      return new InterpreterResult(InterpreterResult.Code.ERROR,
        "Cannot open connection to SQL Server.");
    }

    Statement stmt;
    try {
      stmt = jdbcConnection.createStatement();
      stmt.setMaxRows(_maxRows);

      boolean hasResultSet = stmt.execute(cmd);

      if (hasResultSet) {
        ResultSet resultSet = stmt.getResultSet();
        ResultSetMetaData md = resultSet.getMetaData();

        resultMessage.append(TABLE_MAGIC_TAG);

        int columns = md.getColumnCount();

        // Table Header
        for (int i = 1; i <= columns; i++) {
          resultMessage.append(md.getColumnName(i));
          if (i < columns) resultMessage.append(TAB);
        }
        resultMessage.append(NEWLINE);

        // Table Body
        while (resultSet.next()) {
          for (int i = 1; i <= columns; i++) {
            resultMessage.append(replaceTableSpecialChar(resultSet.getString(i)));
            if (i < columns) resultMessage.append(TAB);
          }
          resultMessage.append(NEWLINE);
        }

      } else {
        int rowsUpdated = stmt.getUpdateCount();
        if (rowsUpdated >= 0)
          resultMessage.append(String.format("%1$d records affected.", rowsUpdated));
        else
          resultMessage.append("Command executed successfully.");
      }
      result = InterpreterResult.Code.SUCCESS;
    }
    catch (SQLException e) {
      _logger.error("Cannot execute SQL Server statement.", e);
      resultMessage = new StringBuilder();
      resultMessage.append("Cannot execute SQL Server statement.").append(NEWLINE);
      resultMessage.append(e.getMessage()).append(NEWLINE);
      result = InterpreterResult.Code.ERROR;
    }

    closeSQLServerConnection(jdbcConnection);

    return new InterpreterResult(result, resultMessage.toString());
  }

  @Override
  public void cancel(InterpreterContext context) {

  }

  @Override
  public FormType getFormType() {
    return FormType.SIMPLE;
  }

  @Override
  public int getProgress(InterpreterContext context) {
    return 0;
  }

  @Override
  public Scheduler getScheduler() {
    return SchedulerFactory.singleton().createOrGetFIFOScheduler(
      SqlServerInterpreter.class.getName() + this.hashCode()
    );
  }

  @Override
  public List<String> completion(String buf, int cursor) {
    return null;
  }
}
