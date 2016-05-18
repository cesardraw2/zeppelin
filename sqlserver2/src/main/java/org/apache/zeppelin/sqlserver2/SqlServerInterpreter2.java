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
package org.apache.zeppelin.sqlserver2;

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
public class SqlServerInterpreter2 extends Interpreter
{
  private static final String VERSION = "2.0.3.1";

  private static final char WHITESPACE = ' ';
  private static final char NEWLINE = '\n';
  private static final char TAB = '\t';
  private static final String TABLE_MAGIC_TAG = "%table ";

  private static final String DEFAULT_JDBC_URL = "jdbc:sqlserver://localhost:1433";
  private static final String DEFAULT_JDBC_USER_PASSWORD = "";
  private static final String DEFAULT_JDBC_USER_NAME = "zeppelin";
  private static final String DEFAULT_JDBC_DATABASE_NAME = "tempdb";
  private static final String DEFAULT_JDBC_DRIVER_NAME =
    "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  private static final String DEFAULT_MAX_RESULT = "1000";

  private static final String SQLSERVER_SERVER_URL = "sqlserver.url";
  private static final String SQLSERVER_SERVER_USER = "sqlserver.user";
  private static final String SQLSERVER_SERVER_PASSWORD = "sqlserver.password";
  private static final String SQLSERVER_SERVER_DATABASE_NAME = "sqlserver.database";
  private static final String SQLSERVER_SERVER_DRIVER_NAME = "sqlserver.driver.name";
  private static final String SQLSERVER_SERVER_MAX_RESULT = "sqlserver.max.result";

  private Logger _logger = LoggerFactory.getLogger(SqlServerInterpreter2.class);
  private int _maxRows = 1000;

  static {
    Interpreter.register(
      "sql",
      "tsql2",
      SqlServerInterpreter2.class.getName(),
      new InterpreterPropertyBuilder()
        .add(SQLSERVER_SERVER_URL, DEFAULT_JDBC_URL, "JDBC URL for SQL Server.")
        .add(SQLSERVER_SERVER_USER, DEFAULT_JDBC_USER_NAME, "SQL Server user name")
        .add(SQLSERVER_SERVER_PASSWORD, DEFAULT_JDBC_USER_PASSWORD, "SQL Server user password")
        .add(SQLSERVER_SERVER_DATABASE_NAME, DEFAULT_JDBC_DATABASE_NAME, "SQL Server database")
        .add(SQLSERVER_SERVER_DRIVER_NAME, DEFAULT_JDBC_DRIVER_NAME, "JDBC Driver Name")
        .add(SQLSERVER_SERVER_MAX_RESULT, DEFAULT_MAX_RESULT,
          "Max number of SQL result to display.")
        .build());
  }

  public SqlServerInterpreter2(Properties property) {
    super(property);
  }

  private Connection openSQLServerConnection()
  {
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

    return jdbcConnection;
  }

  private void closeSQLServerConnection(Connection jdbcConnection)
  {
    try
    {
      if (jdbcConnection != null)
        jdbcConnection.close();
    } catch (SQLException e)
    {
      _logger.error("Cannot close connection.", e);
    }
  }

  @Override
  public void open() {
    _logger.info(String.format("Starting T-SQL Interpreter v %1s", VERSION));
    Connection jdbcConnection = openSQLServerConnection();

    if (jdbcConnection != null) {
      _logger.debug("TODO: Load autocomplete.");
      closeSQLServerConnection(jdbcConnection);
    }
  }

  @Override
  public void close() {
    _logger.debug("TODO: Cleanup.");
  }

  @Override
  public InterpreterResult interpret(String cmd, InterpreterContext contextInterpreter) {
    InterpreterResult.Code result;
    StringBuilder resultMessage = new StringBuilder();

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
            resultMessage.append(resultSet.getString(i));
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
      SqlServerInterpreter2.class.getName() + this.hashCode()
    );
  }

  @Override
  public List<String> completion(String buf, int cursor) {
    return null;
  }
}
