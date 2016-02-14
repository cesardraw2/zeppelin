## SQL Server Interpreter for Apache Zeppelin

This interpreter supports the following SQL engines:
* [SQL Server](https://www.microsoft.com/sqlserver) - Relational & Post-Relational database management system (RDBMS)
* [Azure SQL Database](https://azure.microsoft.com/it-it/services/sql-database) - A Relational database-as-a-service in the cloud

### Build notes

In order to build and use the SQL Server Apache Zeppelin Interpreter, the Microsoft JDBC drivers needs to be installed.
This build has been tested with the JDBC SQL Server driver 4.1 that comes with the latest JDBC package, the 6.0.

The latest driver are here:

* [Microsoft JDBC Driver for SQL Server](https://msdn.microsoft.com/en-us/data/aa937724.aspx)

To download the latest driver you can just curl it from a terminal window:

```
curl -L "https://download.microsoft.com/download/0/2/A/02AAE597-3865-456C-AE7F-613F99F850A8/sqljdbc_6.0.6629.101_enu.tar.gz" | tar xz
```

at the end of the process you'll have a sqljdbc_6.0 folder in your home.

Here's some useful link on JDBC and how to use it:

* [Overview of the JDBC Driver](https://msdn.microsoft.com/en-US/library/ms378749.aspx)
* [Deploying the JDBC Driver](https://msdn.microsoft.com/en-US/library/aa342329.aspx)
* [Using the JDBC Driver](https://msdn.microsoft.com/en-US/library/ms378526.aspx)
* [Building the Connection URL](https://msdn.microsoft.com/en-us/library/ms378428.aspx)

For Azure:

* [Connecting to an Azure SQL database](https://msdn.microsoft.com/en-us/library/hh290696.aspx)

In order to have Maven correctly recognize the dependency, you have to register JDBC into Maven local repository. From
a terminal window:

```
mvn install:install-file -Dfile=~/sqljdbc_6.0/enu/sqljdbc41.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc41 -Dversion=4.1  -Dpackaging=jar -DgeneratePom=true
```

### Additional notes

Given then I'm not a Java Expert, the interpreter has been created starting from the postgresql interpreter code base. This means that the interpreter at present time "just works", but it really needs to be rewritten from scratch in order to cleanup the code to make sure that both the JDBC Driver and SQL Server are used properly and efficiently. This will be done in the next release, for now this is more an Alpha version to check how such interpreter could work and to get confident with Zeppelin codebase and environment.
