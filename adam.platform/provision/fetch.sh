#!/usr/bin/env bash

# fetches all required packages and copies them to
# the relevant 'adam/roles/role/files' folders

ROOT='adam/roles/'

# creates the root folder if it does not exist
if [[ ! -e $ROOT ]]; then
    mkdir $ROOT
fi

download() {
    # $1: download URI root
    # $2: filename
    # $3: target folder for downloaded file
    # $4: http header
    if [[ ! -e $3 ]]; then
        mkdir $3
    fi
    if [[ ! -e $3$2 && $4 ]]; then
        wget --header "$4" -O $3$2 $1$2
    elif [[ ! -e $3$2 ]]; then
        wget -O $3$2 $1$2
    fi
}

download_name() {
    # $1: download full URI including filename
    # $2: new filename
    # $3: target folder for downloaded file
    if [[ ! -e $3 ]]; then
        mkdir $3
    fi
    if [[ ! -e $3$2 ]]; then
        wget -O $3$2 $1
    fi
}

# download and unzips the mysql connector jar
download_mysql_connector() {
    ROLE="wildfly/files/"
    CONN_FOLDER="mysql-connector-java-5.1.36"
    CONN_FILE="mysql-connector-java-5.1.36.tar.gz"
    if [[ ! -e $ROOT$ROLE$CONN_FILE ]]; then
        download "http://dev.mysql.com/get/Downloads/Connector-J/" $CONN_FILE $ROOT$ROLE
        tar -zxf $ROOT$ROLE$CONN_FILE -C $ROOT$ROLE *.jar
        mv $ROOT$ROLE$CONN_FOLDER"/mysql-connector-java-5.1.36-bin.jar" $ROOT$ROLE
        rm -r $ROOT$ROLE$CONN_FOLDER
    fi
}

# download the following files to the root folder if they do not exist
download "http://download.oracle.com/otn-pub/java/jdk/8u51-b16/" "jdk-8u51-linux-x64.rpm" $ROOT"java/files/" "Cookie: oraclelicense=accept-securebackup-cookie"
download "http://download.jboss.org/wildfly/9.0.0.Final/" "wildfly-9.0.0.Final.zip" $ROOT"wildfly/files/"
download "http://mirror.ox.ac.uk/sites/rsync.apache.org/activemq/5.11.1/" "apache-activemq-5.11.1-bin.tar.gz" $ROOT"activemq/files/"
download_name "http://repo1.maven.org/maven2/org/apache/activemq/activemq-rar/5.11.1/activemq-rar-5.11.1.rar" "activemq-rar.rar" $ROOT"wildfly/files/"
download_mysql_connector

ADAM_WAR_NAME='adam.svc-0.0.1-SNAPSHOT.war'
ADAM_WAR_TARGET=$ROOT'wildfly/files/adam.war'
ADAM_WAR_URI='../../adam.svc/build/libs/'$ADAM_WAR_NAME

if [[ ! -e $ADAM_WAR_TARGET ]]; then
    if [[ ! -e $ADAM_WAR_URI ]]; then
        echo ADAM WAR not found, building it...
        SCRIPTPATH="$PWD"
        cd ../../adam.svc
        gradle build
        cd $SCRIPTPATH
    fi
    echo Copying the ADAM WAR $ADAM_WAR_TARGET file...
    cp $ADAM_WAR_URI $ADAM_WAR_TARGET
fi