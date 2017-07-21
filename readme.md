# readme

## 开发中...要点记录

#### crawler engine

- 关于初始化任务队列
    - 由单独的进程进行，与采集系统互不影响，便于控制轮询时间
    - 每种媒体类型对应一个任务队列，命名："TASK_QUEUE_${mediaType}"

- 获取任务(HtmlInfo task)
    - 必要信息：
        - url
        - mediaType: 媒体类型(新闻搜索、微博监控等)
        - siteFlag: 站点标识(baidu)
        - crawlType: 采集类型(META/DATA)
        - searchKey: 记录关键词信息(categoryCode等)
        - data: meta数据
        - encode: 目标页面编码

#### mybatis注意事项

mybatis 插入数据，oracle通过序列生成自增主键，setId()给实体

sql的每个字段，实体对应的属性都必须有值，否则无法写入数据库
在service.saveData()中添加了数据检查，Override此方法以实现在保存数据前自定义检查数据完整性。


## 架构

- 任务队列：redis-cluster[√]
- url过滤：bloom filter redis[√]
- CrawlerClient
    - downloader
    - extractor
    - save
- 持久层：mybatis[]
    - BaseService[√]
    - WeixinService[√]
    - ...
- 配置管理：zookeeper



## 配置
src/main/resource/

- config.properties 系统配置
- 通常不需要修改的配置
    
    - daoConf/: 存放mybatis mappers
    - typeConf/: 类型属性配置
    - xpathConf/: 站点xpath配置
    - app-sysconfig.xml: spring配置文件
    - crawlerlog.properties: 强哥的日志组件配置
    - img.dic&invalid.dic: 无视
    - simplelogger.properties: 日志配置

## 开发

#### 持久层

1. 新建 XxxMapper.java
2. 实现 XxxService.java
3. 新建 mapper-xxx.xml
4. 修改 app-sysconfig.xml，```xxxMapper和xxxService```两个bean
5. 测试

## todo

对保存成功的数据进行bloomfilter add处理
初始化bloomfilter耗时太长，修改方案：
- 初始化功能从爬虫客户端分离出来，独立进行
- 读数据库，获得游标，遍历的同时add进bloomfilter，不要select 装配到list之后再遍历
- 可以配合sql语句，启用多个线程初始化bf，提升效率