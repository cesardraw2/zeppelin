# Zeppelin for SQL Server

This fork of Apache Zepplin is focused on specific support for SQL Server and SQL Azure. Please refer to Apache Zepplin main page for general information on the project:

[Apache Zepplin](https://github.com/apache/incubator-zeppelin)

## Requirements
 * Java 1.7
 * Tested and Build on Ubuntu 15.10
 * Maven (if you want to build from the source code)
 * Node.js Package Manager

## Getting Started

### Before Build
The installation method may vary according to your environment, example is for Ubuntu 15.10.
You can download Ubuntu from here: http://www.ubuntu.com/download/desktop/.

The current version has been built and tested on Ubutu 15.10 64bits.

From a terminal shell:

```
# install packages
sudo apt-get update
sudo apt-get install git
sudo apt-get install openjdk-7-jdk
sudo apt-get install npm
sudo apt-get install libfontconfig
sudo apt-get install maven

# install Microsoft JDBC
curl -L "https://download.microsoft.com/download/0/2/A/02AAE597-3865-456C-AE7F-613F99F850A8/sqljdbc_6.0.6629.101_enu.tar.gz" | tar xz
```

please note that the above commands already contains anything needed in order to make Zeppelin work with SQL Server.
If you want to have more information, you can take a look at the readme in the ```sqlserver``` folder.

### Get Source Code

Download code from GitHub. From a terminal shell:

```
git clone https://github.com/yorek/incubator-zeppelin.git zeppelin-sqlserver
```

### Build

From a terminal shell:

```
export CLASSPATH=~/sqljdbc_6.0/enu/sqljdbc41.jar
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=1024m"

mvn install:install-file -Dfile=~/sqljdbc_6.0/enu/sqljdbc41.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc41 -Dversion=4.1  -Dpackaging=jar -DgeneratePom=true

cd ~/zeppelin-sqlserver

mvn clean package -DskipTests

cp ./conf/zeppelin-site.xml.template ./conf/zeppelin-site.xml
cp ./conf/zeppelin-env.sh.template ./conf/zeppelin-env.sh

cp ~/sqljdbc_6.0/enu/sqljdbc41.jar ~/zeppelin-sqlserver/zeppelin-interpreter/target/lib
```

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

WIP

### Configuring the Interpreter

WIP

### Creating a Notebook

WIP