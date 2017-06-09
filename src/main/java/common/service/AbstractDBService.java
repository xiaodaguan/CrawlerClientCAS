package common.service;

import common.bean.CommonData;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.system.SiteTemplateAttribute;
import common.system.Systemconfig;
import common.system.UserAttribute;
import common.util.UserAgent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public abstract class AbstractDBService<T> implements DBService<T> {

    protected JdbcTemplate jdbcTemplate;

    private static final String LOG_TABLE = "sys_log";
    private static final String updateSql = "insert into " + LOG_TABLE + " (ip, name, type, time, content, status) values (?,?,?,?,?,?)";


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public abstract List<SearchKey> searchKeys();

    @Override
    public void saveDatas(List<T> list) throws IOException {
        for (T t : list) {
            saveData(t);
        }
    }

    @Override
    public void saveCommentDatas(List<T> list) throws IOException {
        for (T t : list) {
            saveCommentData(t);
        }
    }

    @Override
    public List<? extends CommonData> getNorepeatData(List<? extends CommonData> list, String table) {
        Iterator<? extends CommonData> iter = list.iterator();
        List<CommonData> repeatDatas = new ArrayList<CommonData>();
        while (iter.hasNext()) {
            CommonData cd = iter.next();
            if (!Systemconfig.urm.checkNoRepeat(cd.getMd5())) {
                iter.remove();
                repeatDatas.add(cd);
            }
        }
        return repeatDatas;
    }

    @Override
    public void deleteReduplicationUrls(List<String> urlList, String table) {
        table = table.replace("person_data", "leaders");
        String sql = "select id from " + table + " where md5=?";
        String DELETE_SQL = "delete from " + table + " where id=?";
        Iterator<String> urlIter = urlList.iterator();
        while (urlIter.hasNext()) {
            String url = urlIter.next();
            synchronized (urlList) {
                urlIter.remove();
            }
            List<Long> idList = this.jdbcTemplate.queryForList(sql, new Object[]{url}, Long.class);
            if (idList.size() > 1) {
                for (int i = idList.size() - 2; i >= 0; i--) {// 只留一条数据
                    this.jdbcTemplate.update(DELETE_SQL, new Object[]{idList.get(i)});
                }
            }
        }
    }

    @Override
    public int getAllMd5(String table, Map<String, List<String>> map) {
        int num = 0;
        String[] tabs = table.split(",");
        try {
            for (String t : tabs) {
            	
            	String tt = t.replace("ebusiness", "eb").replace("person_data", "leaders") ;
            	String SQL_WEB = "select md5 from " + tt;

                
                		
                LOGGER.info("md5 SQL: "+SQL_WEB);
                List<String> list = this.jdbcTemplate.queryForList(SQL_WEB, String.class);
                LOGGER.info("md5 获取成功！！！ ");
                num += list.size();
                map.put(t, list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    @Override
    public void exceptionData(String md5, String table) {
        String esql = "update " + table + " set fail_count=fail_count+1 where md5=?";
        this.jdbcTemplate.update(esql, new Object[]{md5});
    }

    private static String user_sql = "select name, pass, siteflag, id, cookie, ua from " + "(select u.name, u.pass, ss.siteflag, u.id, u.cookie, u.ua, rownum from crawler_account u, site_template ss" + " where u.site_id=ss.id and u.valid=1 and ss.siteflag=? order by last_used) ";
    
    
    
    
    
    @Override
    public void updateUserOrder(String userName) {
        String sql = "update crawler_account set last_used = ? where name = ?";
        this.jdbcTemplate.update(sql, new Timestamp(System.currentTimeMillis()), userName);
        LOGGER.info("account:{" + userName + "} last used updated.");
    }


    @Override
    public void updateUserValid(String userName,int mark) {
//        String sql = "update crawler_account set valid = ? where name = ?";
//        this.jdbcTemplate.update(sql, mark, userName);
//        if(mark==5){
//            LOGGER.info("account:{" + userName + "} Search verification code problem");
//        }else if(mark==6){
//            LOGGER.info("account:{" + userName + "} Login verification code problem");
//        }
//        LOGGER.info("account:{" + userName + "} valid updated.");
    }

    @Override
    public List<UserAttribute> getLoginUsers(String site) {
        site = site.substring(0, site.indexOf("_"));
        final List<UserAttribute> list = new ArrayList<UserAttribute>();
        
        if(Systemconfig.crawlerType==8&&user_sql.contains("u.valid=1")){
        	//搜索	crawlerType = 7  u.valid=1
        	//垂直监控  crawlerType = 8  u.valid=2
        	user_sql = user_sql.replace("u.valid=1","u.valid=2");
        }
        if(Systemconfig.crawlerType==8&&user_sql.contains("u.valid=1")){
        	//搜索	crawlerType = 7  u.valid=1
        	//垂直监控  crawlerType = 8  u.valid=2
        	user_sql = user_sql.replace("u.valid=1","u.valid=2");
        }
        
        if(Systemconfig.crawlerNum!=0&&user_sql.contains("u.valid=1")){
        	//搜索	crawlerType = 7  u.valid=1
        	//垂直监控  crawlerType = 8  u.valid=2
        	user_sql = user_sql.replace("u.valid=1","u.valid="+(Systemconfig.crawlerNum*2-1));
        }
        
        LOGGER.info("loading accounts...: " + user_sql);
        this.jdbcTemplate.query(user_sql, new Object[]{site}, new RowMapper<UserAttribute>() {
            @Override
            public UserAttribute mapRow(ResultSet rs, int i) throws SQLException {
                UserAttribute ua = new UserAttribute();
                ua.setName(rs.getString(1));
                ua.setPass(rs.getString(2));
                ua.setSiteFlag(rs.getString(3));
                ua.setId(rs.getInt(4));
                ua.setCookie(rs.getString(5));
                ua.setUserAgent(rs.getString(6));
                ua.setUsed(0);
                ua.setAgentIndex(UserAgent.getUserAgentIndex());
                if (ua.getUserAgent() == null)
                    ua.setUserAgent(UserAgent.getUserAgent(ua.getAgentIndex()));
                list.add(ua);

                return ua;
            }
        });

        LOGGER.info("[" + list.size() + "] accounts. ");
        return list;
    }

    @Override
    public Map<String, SiteTemplateAttribute> getXpathConfig() {

        String sql = "select ID,SITEFLAG, TEMPLATE_CONTENT_SITE, TEMPLATE_LAST_MODIFIED from SITE_TEMPLATE where template_status = 2 and MEDIA=" + ((Systemconfig.crawlerType + 1) / 2) + " and TYPE=" + ((Systemconfig.crawlerType + 1) % 2);// 奇数搜索
        System.out.println(sql);

        final Map<String, SiteTemplateAttribute> list = new HashMap<String, SiteTemplateAttribute>();

        this.jdbcTemplate.query(sql, new RowMapper<SiteTemplateAttribute>() {
            public SiteTemplateAttribute mapRow(ResultSet rs, int i) throws SQLException {
                SiteTemplateAttribute sta = new SiteTemplateAttribute();
                sta.setTemplateName(CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase());
                sta.setSiteFlag(rs.getString("SITEFLAG"));
                // sta.setType(rs.getString("TYPE"));
                sta.setLastModified(rs.getTimestamp("TEMPLATE_LAST_MODIFIED"));
                // sta.setMedia(rs.getString("MEDIA"));
                sta.setContent(rs.getString("TEMPLATE_CONTENT_SITE"));
                sta.setId(rs.getInt("ID"));
                list.put(sta.getSiteFlag() + "_" + sta.getTemplateName(), sta);

                return sta;
            }
        });
        return list;
    }

    @Override
    public String getTypeConfig() {

        String monitorOrSearch = Systemconfig.crawlerType % 2 == 0 ? "MONITOR_TEMPLATE" : "SEARCH_TEMPLATE";
        String sql = "SELECT " + monitorOrSearch + " from MEDIA_TYPE where id = " + ((Systemconfig.crawlerType + 1) / 2);
        LOGGER.info(sql);
        String result = this.jdbcTemplate.queryForObject(sql, String.class);
        return result;

    }

}
