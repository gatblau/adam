#!/usr/bin/env bash

# fetches all required packages and copies them to
# the relevant 'adam/roles/role/files' folders

ROOT='adam/roles/'

JDK_TARGET=$ROOT'java/files/jdk-8u45-linux-x64.rpm'

JDK_URI='http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jdk-8u45-linux-x64.rpm'

WILDFLY_TARGET=$ROOT'wildfly/files/wildfly-9.0.0.Final.zip'
WILDFLY_URI='http://download.jboss.org/wildfly/9.0.0.Final/wildfly-9.0.0.Final.zip'

ACTIVEMQ_BROKER_TARGET=$ROOT'activemq/files/apache-activemq-5.11.1-bin.tar.gz'
ACTIVEMQ_BROKER_URI='http://mirror.ox.ac.uk/sites/rsync.apache.org/activemq/5.11.1/apache-activemq-5.11.1-bin.tar.gz'

ACTIVEMQ_RAR_TARGET=$ROOT'wildfly/files/activemq-rar.rar'
ACTIVEMQ_RAR_URI='http://repo1.maven.org/maven2/org/apache/activemq/activemq-rar/5.11.1/activemq-rar-5.11.1.rar'


JDBC_CONNECTOR_TARGET=$ROOT'wildfly/files/mysql-connector-java-5.1.36.tar.gz'
JDBC_CONNECTOR_URI='http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.36.tar.gz'

ADAM_WAR_NAME='adam.svc-0.0.1-SNAPSHOT.war'
ADAM_WAR_TARGET=$ROOT'wildfly/files/adam.war'
ADAM_WAR_URI='../../adam.svc/build/libs/'$ADAM_WAR_NAME

if [[ ! -e $JDK_TARGET ]]; then
    echo Downloading the JDK $JDK_TARGET...
    curl -L --header 'Cookie: oraclelicense=accept-securebackup-cookie' $JDK_URI -o $JDK_TARGET
fi

if [[ ! -e $WILDFLY_TARGET ]]; then
    echo Downloading the Wildfly package $WILDFLY_TARGET...
    curl -L $WILDFLY_URI -o $WILDFLY_TARGET
fi

if [[ ! -e $ACTIVEMQ_BROKER_TARGET ]]; then
    echo Downloading the ACTIVEMQ package $ACTIVEMQ_BROKER_TARGET...
    curl -L $ACTIVEMQ_BROKER_URI -o $ACTIVEMQ_BROKER_TARGET
fi

if [[ ! -e $ACTIVEMQ_RAR_TARGET ]]; then
    echo Downloading the ACTIVEMQ RAR $ACTIVEMQ_RAR_TARGET...
    curl -L $ACTIVEMQ_RAR_URI -o $ACTIVEMQ_RAR_TARGET
fi

if [[ ! -e $JDBC_CONNECTOR_TARGET ]]; then
    echo Downloading the JDBC connector file $JDBC_CONNECTOR_TARGET...
    curl -L $JDBC_CONNECTOR_URI -o $JDBC_CONNECTOR_TARGET
    echo Untarring connector
    tar -zxf $JDBC_CONNECTOR_TARGET -C $ROOT'wildfly/files' *.jar
    echo cleaning up
    mv $ROOT'wildfly/files/mysql-connector-java-5.1.36/mysql-connector-java-5.1.36-bin.jar' $ROOT'wildfly/files'
    rm -r $ROOT'wildfly/files/mysql-connector-java-5.1.36'
fi

if [[ ! -e $ADAM_WAR_TARGET ]]; then
    if [[ ! -e $ADAM_WAR_URI ]]; then
        echo ADAM WAR not ound, building it...
        SCRIPTPATH="$PWD"
        cd ../../adam.svc
        gradle build
        cd $SCRIPTPATH
    fi
    echo Copying the ADAM WAR $ADAM_WAR_TARGET file...
    cp $ADAM_WAR_URI $ADAM_WAR_TARGET
fi