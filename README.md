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
If you don't have requirements prepared, install it.
(The installation method may vary according to your environment, example is for Ubuntu 15.10.).

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

### Get Source Code

Download code from GitHub. From a terminal shell:

```
git clone https://github.com/yorek/incubator-zeppelin.git zeppelin-sqlserver
```

### Build

```
export CLASSPATH=/home/dmauri/sqljdbc_6.0/enu/sqljdbc41.jar
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=1024m"

cd ~/zeppelin-sqlserver

mvn clean package -DskipTests

cp ./conf/zeppelin-site.xml.template ./conf/zeppelin-site.xml
cp ./conf/zeppelin-env.sh.template ./conf/zeppelin-env.sh

cd ~/

cp ./sqljdbc_6.0/enu/sqljdbc41.jar ./zeppelin-sqlserver/zeppelin-interpreter/target/lib

```

### Configure
If you wish to configure Zeppelin option (like port number), configure the following files:

```
./conf/zeppelin-env.sh
./conf/zeppelin-site.xml
```
