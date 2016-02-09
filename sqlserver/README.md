## SQL Server Interpreter for Apache Zeppelin

This interpreter supports the following SQL engines:
* [SQL Server](https://www.microsoft.com/sqlserver) - Relational & Post-Relational database management system (RDBMS)
* [Azure SQL Database](https://azure.microsoft.com/it-it/services/sql-database) - A Relational database-as-a-service in the cloud

### Installation notes

In order to build and use the SQL Server Apache Zeppelin Interpreter, the Microsoft JDBC drivers needs to be installed.

Download the latest driver here:

* [Microsoft JDBC Driver for SQL Server](https://msdn.microsoft.com/en-us/data/aa937724.aspx)

Here's some useful link for doing that:

* [Overview of the JDBC Driver](https://msdn.microsoft.com/en-US/library/ms378749.aspx)
* [Deploying the JDBC Driver](https://msdn.microsoft.com/en-US/library/aa342329.aspx)
* [Using the JDBC Driver](https://msdn.microsoft.com/en-US/library/ms378526.aspx)
* [Building the Connection URL](https://msdn.microsoft.com/en-us/library/ms378428.aspx)

For Azure:

* [Connecting to an Azure SQL database](https://msdn.microsoft.com/en-us/library/hh290696.aspx)

in order to have JDBC running correctly, the SQL Server JDBC class must be available to Java, You can to that by setting your CLASSPATH environment:

```sh
export CLASSPATH=/home/johndoe/sqljdbc_6.0/enu/sqljdbc41.jar
```

The build has been tested with JDBC SQL Server driver 4.1
