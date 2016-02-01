# readme

/*组内爬虫程序*/

- 目录：

config/	配置文件目录

_ ./site 站点采集属性配置

_ ./app-sysconfig.xml 爬虫结构属性配置

_ ./config.properties 爬虫运行属性配置

site/ 站点采集模板(新配置站点已经采用数据库存储)

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





- 运行

配置好config.properties：

	- 数据源
	- 采集类型
	- 配置读取方式
	- 运行方式

	启动CrawlerStart.java
-


