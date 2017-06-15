# readme

## 注意
1. 微信爬虫运行需要系统装有firefox浏览器

## 配置  

config.properties：  

	- 数据源    
	- 配置读取方式 0:from xml file, 1: from database  
	- 运行方式: run/test  

### 需要登录的站点  

将账号配置在crawler_account表中  
site_id与site_template表中对应模板的id一致  

### 垂直监控配置  

将要监控的url配置在monitor_site中,注意site_name  

## 爬虫启动

#### maven 打包运行方式

1. 本地maven库安装第三方依赖jar

```
//project 目录下运行命令
mvn install:install-file -Dfile=lib/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/chardet-1.0.jar -DgroupId=org.mozilla.intl -DartifactId=chardet -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/crwlerlog.jar -DgroupId=crwlerlog -DartifactId=crwlerlog -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/hadoop-core-1.0.4.jar -DgroupId=org.apache -DartifactId=hadoop -Dversion=1.0.4 -Dpackaging=jar
mvn install:install-file -Dfile=lib/hbase-0.94.16-security.jar -DgroupId=org.apache.hadoop -DartifactId=hbase -Dversion=0.94.16 -Dpackaging=jar
mvn install:install-file -Dfile=lib/mail.jar -DgroupId=com.sun.mail -DartifactId=mail -Dversion=1.0 -Dpackaging=jar
```

2. 打包 mvn clean package
3. 复制 config 目录到target/
```
cp -r config target/
```
4. 运行
java -jar CrawlerClientCAS-1.0.jar type=15 name=test project=66666



- 参数  

```
type=n
```
>1: 新闻搜索; 2: 新闻垂直; 3: 论坛搜索; 4: 论坛垂直; 5: 博客搜索; 6: 博客垂直; 7: 微博搜索; 8: 微博垂直; 9：视频搜索;  13: 电商搜索; 14: 电商垂直; 15: 微信搜索; 16: 微信垂直; 21: 上市公司报告搜索; 34: 公司信息垂直 ;  37: 政务搜索;   39: 刊物搜索; 41: 外媒搜索 ;45：客户端搜索


```
crawlercount=n   
```
>需要分布式部署多少个爬虫

```
clientindex=n   
```
>当前是几号爬虫,同时该字段也对应与微博爬虫的账号选择，
选择方式是，clientindex = int[(crawler_account.valid+1)/2] 



_组内爬虫程序_

## 目录：

config/	配置文件目录

_ ./typeConf 站点采集属性配置

_ ./app-sysconfig.xml 爬虫结构属性配置

_ ./config.properties 爬虫运行属性配置

typeConf/ 站点采集模板(新配置站点已经采用数据库存储)

src/

_ ./common

_ _ ./bean

_ _ ./communicate

_ _ ./down

_ _ ./extractor

_ _ ./filter

_ _ ./http

_ _ ./rmi

_ _ ./service

_ _ ./siteinfo

_ _ ./system

_ _ ./up2hdfs

_ _ ./util

_ _ CrawlerStart.java


注：
增加了代码的分布式运行方式




