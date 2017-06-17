mvn install:install-file -Dfile=lib/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/chardet-1.0.jar -DgroupId=org.mozilla.intl -DartifactId=chardet -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/crwlerlog.jar -DgroupId=crwlerlog -DartifactId=crwlerlog -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/hadoop-core-1.0.4.jar -DgroupId=org.apache -DartifactId=hadoop -Dversion=1.0.4 -Dpackaging=jar
mvn install:install-file -Dfile=lib/hbase-0.94.16-security.jar -DgroupId=org.apache.hadoop -DartifactId=hbase -Dversion=0.94.16 -Dpackaging=jar
mvn install:install-file -Dfile=lib/mail.jar -DgroupId=com.sun.mail -DartifactId=mail -Dversion=1.0 -Dpackaging=jar
