# readme

## 开发中...


2017-06-16

mybatis 插入数据，oracle通过序列生成自增主键，setId()给实体

sql的每个字段，实体对应的属性都必须有值，否则无法写入数据库



## 架构

- 任务队列：redis-cluster  
- url过滤：bloom filter redis[√]
- CrawlerClient
- 持久层：mybatis[进行中]
    - BaseService[√]
    - WeixinService[]
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
照猫画虎四步走
1. 新建 XxxMapper.java
2. 实现 XxxService.java
3. 新建 mapper-xxx.xml
4. 修改 app-sysconfig.xml，```xxxMapper和xxxService```两个bean
5. 测试

## todo

对保存成功的数据进行bloomfilter add处理