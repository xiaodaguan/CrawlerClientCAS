2016-01-08
	v0.95
		博客：
			修复博客采集不到时间的bug
			去除博客标题中的日期
		微信：
			修复微信舆情平台搜索的运行bug
2016-01-07
	v0.95
		修复微博采集不到头像的bug


2016-01-05
	v0.95 增加媒体类型：人物(31,32)
		存储于: leaders表
		搜索配置：search_keyword表中不必配置category_code，采集时，若搜索词在category_scheme表中已存在，则category_scheme.id -> leaders.category_id，否则category_scheme表新增一个记录，并存储leaders.category_id
		读取关键词时仅读取type=16的关键词
		新增package，在以下位置: down, xpath, service
		修改app-sysconfig.xml文件: dbservice属性，添加人物服务
		增加site(db.site_template): person_search_xxx
		增加media_type -> db.media_type: person
			search template
			*monitor template(暂无需求)


2015-12-25
	v0.94 增加媒体类型：会议
		增加一个会议网站采集配置：中金展会中心
	增加博客搜索：和讯博客
	增加test模式：只读取debug为1的关键词


2015-11-03 15:52:03

	v0.91版本，准备加入ip池策略
	
	增加农业：蔬菜价格采集(已完成2015-12-03 09:51:41)
	
	修复微信公众号监控功能(已完成2015-11-16 10:33:11)
	（如成功，删除0.90版本）
	
	需注意：
	1. 此版本后，站点配置全部从数据库读取（readConfigType=1）
	2. 站点配置中，域名必须准确，domainId必须准确 