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
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.scheduler.Scheduler;
import org.apache.zeppelin.scheduler.SchedulerFactory;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL Server interpreter v2 for Zeppelin.
 */
public class SqlServerInterpreter2 extends Interpreter
{
  Logger logger = LoggerFactory.getLogger(SqlServerInterpreter2.class);

  static {
    register("tsql2", SqlServerInterpreter2.class.getName());
  }

  public SqlServerInterpreter2(Properties property) {
    super(property);
  }

  @Override
  public void open() {

  }

  @Override
  public void close() {

  }

  @Override
  public InterpreterResult interpret(String cmd, InterpreterContext contextInterpreter) {
    logger.debug("T-SQL command: '" + cmd + "'");
    return new InterpreterResult(InterpreterResult.Code.SUCCESS, "dummy");
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
