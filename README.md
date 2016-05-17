# Zeppelin for SQL Server

This fork of Apache Zeppelin is focused on specific support for SQL Server and SQL Azure. Please refer to Apache Zeppelin main page for general information on the project:

[Apache Zeppelin](https://github.com/apache/incubator-zeppelin)

## Requirements
 * Java 1.7
 * Tested and Build on Ubuntu 16.04 LTS
 * Maven (if you want to build from the source code)
 * Node.js Package Manager

## Getting Started

### Before Build
The installation method may vary according to your environment, example is for Ubuntu 15.10.
You can download Ubuntu from here: http://www.ubuntu.com/download/desktop/.

The current version has been built and tested on Ubuntu 16.04 LTS 64bits.

From a terminal shell:

```
# install packages
sudo apt-get update
sudo apt-get install git
sudo apt-get install openjdk-8-jdk
sudo apt-get install npm
sudo apt-get install libfontconfig
sudo apt-get install maven

# get Microsoft JDBC
curl -L "https://download.microsoft.com/download/0/2/A/02AAE597-3865-456C-AE7F-613F99F850A8/sqljdbc_6.0.6629.101_enu.tar.gz" | tar xz
```

### Get Source Code

Download code from GitHub. From a terminal shell:

```
git clone https://github.com/yorek/incubator-zeppelin.git zeppelin-sqlserver
```

This will clone the GitHub repository into a folder named ```zeppelin-sqlserver``` in your home directory

### Build

From a terminal shell:

```
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=1024m"

mvn install:install-file -Dfile=sqljdbc_6.0/enu/sqljdbc41.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc41 -Dversion=4.1  -Dpackaging=jar -DgeneratePom=true

cd ~/zeppelin-sqlserver

mvn clean package -DskipTests

cp ./conf/zeppelin-site.xml.template ./conf/zeppelin-site.xml
cp ./conf/zeppelin-env.sh.template ./conf/zeppelin-env.sh
```

Please note that the above commands already contains anything needed in order to make Zeppelin work with SQL Server.
If you want to have more information on the SQL Server interpreter, you can take a look at the readme in the ```sqlserver``` folder:

[SQL Server Interpreter for Apache Zeppelin](https://github.com/yorek/incubator-zeppelin/blob/master/sqlserver/README.md)

### Configure

If you wish to configure Zeppelin option (like port number), configure the following files:

```
./conf/zeppelin-env.sh
./conf/zeppelin-site.xml
```

### Start Zeppelin

From a terminal shell, start Zeppelin Daemon:

```
./bin/zeppelin-daemon.sh start
```

you can now head to ```http://localhost:8080``` to see Zeppelin running.

## Using Zeppelin

### Create and configure the Interpreter

Click on Interpreter menu item so that Zeppelin will show you the Interpreters page.

Scroll to the bottom of the page to find the ```tsql``` interpreter. Click on the ```edit``` button on the right and fill the properties with the values correct for the SQL Server or SQL Azure instance you'd like to connect to. The property ```sqlserver.driver.name``` is already set to the correct value. Change it *only* if you really know what you're doing.

the ```sqserver.url``` parameter is more or less the equivalent of a connection string in .NET. To connect to a local SQL Server it will be something like:

```
jdbc:sqlserver://<your-local-sql-server-address>:1433
```

to connect to SQL Azure it will be similar to:

```
jdbc:sqlserver://<your-sql-azure-server-name>.database.windows.net:1433
```

Now click on save and now you're ready to use the configured SQL Server interpreter in a Notebook.

If you want to create a new SQL Server interpreter to connect to a different SQL Server, just click on the ```+ Create``` button on the top right at the beginning of the page. Type a name for your interpreter, for example, "SQL Server" and from the interpreter drop-down menu select the ```tsql``` item. Now you can follow the same procedure described above to configure your new interpreter.

### Creating a Notebook

On the ```Notebook``` menu, select the ```+ Create new note``` item. Give the notebook the name you prefer, for example "SQL Azure".

Now you have to choose which interpreter you want to use among all the ones available. To do so, click on the gear icon on the right, near the ```default``` button.
The selected interpreter, which will be available to use in your notebook, will be in light blue. The deselected one will be shown in light gray. You should have all the interpreter already selected. If you want to change something, click one the interpreter you want to enable or disable to do so. Just make sure that the ```tsql``` interpreter is selected. Save your choices by pressing on the ```Save``` button.

### Using a Notebook

Now click on the white box on the top, and you'll be able to write your first query. Something like:

```
%tsql
select @@version
```

will be enough to make sure that SQL Server interpreter is working correctly. The first line tells
Zeppelin that you're going to send something that has to be interpreted by the SQL Server Interpreter. The second one simply ask to SQL Server to return server name and version info.
Tu run the code, just hit ```Shift + Enter```

Welcome to the Apache Zeppelin world!
