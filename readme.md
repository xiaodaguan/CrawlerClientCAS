# readme  

## 配置  

config.properties：  

	- 数据源  
	- 采集类型  
	- 配置读取方式  
	- 运行方式  
	
## 爬虫启动  
- 参数  

```
type=n
```
>1: 新闻搜索; 2: 新闻垂直; 3: 论坛搜索; 4: 论坛垂直; 5: 博客搜索; 6: 博客垂直; 7: 微博搜索; 8: 微博垂直; 13: 电商搜索; 14: 电商垂直; 15: 微信搜索; 16: 微信垂直; 21: 上市公司报告搜索; 34: 公司信息垂直 






_组内爬虫程序_

## 目录：

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





