package common.service.oracle;

import common.bean.*;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.service.AbstractDBService;
import common.system.Systemconfig;
import org.apache.http.HttpHost;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

/**
 * 数据库操作
 *
 * @author grs
 */
public abstract class OracleService<T> extends AbstractDBService<T> {
    @Override
    public void saveCommentData(T t) throws IOException {

    }

    @Override
    public void updateStatus(final CrawlerStatus cs, final CrawlerTaskStatus cts, final SearchKey key, final int type) {
//        if (type == 1) {// init
//            final String sql = "insert into crawler_status(media_type, start_time, status, keyword_all, ip, crawler_name, insert_time, keyword_count, complete_count) values (?,?,?,?,?,?,?,?,?)";
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            this.jdbcTemplate.update(new PreparedStatementCreator() {
//                @Override
//                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                    PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
//
//                    ps.setInt(1, cs.getCrawlerType());
//                    ps.setTimestamp(2, new Timestamp(cs.getStartTime().getTime()));
//                    ps.setString(3, cs.getStatus());
//                    ps.setString(4, cs.getStartKeywordSet().toString());
//                    ps.setString(5, cs.getIp());
//                    ps.setString(6, cs.getCrawlerName());
//                    ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
//                    ps.setInt(8, cs.getStartKeywordSet().size());
//                    ps.setInt(9, cs.getCrawedCount());
//                    return ps;
//                }
//            }, keyHolder);
//
//            cs.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
//
//        } else if (type == 2) {// (start)fetching
//
//            final String sql = "insert into crawler_status" + "(media_type, start_time, status, interval, threads, keyword_all, keyword_current, keyword_exist, url_count, ip, crawler_name, task_name, parent_id, insert_time)" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            this.jdbcTemplate.update(new PreparedStatementCreator() {
//                @Override
//                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                    PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
//                    ps.setInt(1, cs.getCrawlerType());
//                    ps.setTimestamp(2, new Timestamp(cts.getStartTime().getTime()));
//                    ps.setString(3, cts.getStatus());
//                    ps.setInt(4, cts.getInterval());
//                    ps.setInt(5, cts.getThreadNum());
//                    ps.setString(6, cs.getStartKeywordSet().toString());
//                    ps.setString(7, cts.getKeyword());
//                    ps.setString(8, cs.getAllKeywords().toString());
//                    ps.setInt(9, cts.getFetchCount());
//                    ps.setString(10, cs.getIp());
//                    ps.setString(11, cs.getCrawlerName());
//                    ps.setString(12, cts.getId());
//                    ps.setInt(13, cs.getId());
//                    ps.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
//                    return ps;
//                }
//            }, keyHolder);
//
//            cts.setDbID(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
//            /* 更新父任务(爬虫)状态 */
//            this.jdbcTemplate.update("update crawler_status set status = ?, keyword_count=?, complete_count=? where id = ?", cs.getStatus(), cs.getStartKeywordSet().size(), cs.getCrawedCount(), cs.getId());
//        } else if (type == 3) {// fetching
//            String sql = "update crawler_status set url_count = ?, insert_time = ? where id = ?";
//            this.jdbcTemplate.update(sql, cts.getFetchCount(), new Timestamp(System.currentTimeMillis()), cts.getDbID());
//            /* 更新父任务(爬虫)状态 */
//            this.jdbcTemplate.update("update crawler_status set status = ?, keyword_count=?, complete_count=? where id = ?", cs.getStatus(), cs.getStartKeywordSet().size(), cs.getCrawedCount(), cs.getId());
//
//        } else if (type == 4) {// downloading
//            String sql = "update crawler_status set status=?, url_count=?, down_count = ?, saved_count=?, insert_time = ? where id = ?";
//            this.jdbcTemplate.update(sql, cts.getStatus(), cts.getFetchCount(), cts.getDownCount(), cts.getSavedCount(), new Timestamp(System.currentTimeMillis()), cts.getDbID());
//            /* 更新父任务(爬虫)状态 */
//            this.jdbcTemplate.update("update crawler_status set status = ?, keyword_count=?, complete_count=? where id = ?", cs.getStatus(), cs.getStartKeywordSet().size(), cs.getCrawedCount(), cs.getId());
//
//        } else if (type == 5) {// (task)complete
//            String sql = "update crawler_status set status = ?, saved_count=?, insert_time = ? where id = ?";
//            this.jdbcTemplate.update(sql, cts.getStatus(), cts.getSavedCount(), new Timestamp(System.currentTimeMillis()), cts.getDbID());
//            /* 更新父任务(爬虫)状态 */
//            this.jdbcTemplate.update("update crawler_status set status = ?, keyword_count=?, complete_count=? where id = ?", cs.getStatus(), cs.getStartKeywordSet().size(), cs.getCrawedCount(), cs.getId());
//
//        } else if (type == 6) {// (crawler)complete
//            String sql = "update crawler_status set status = ?, keyword_count=?, complete_count=?, insert_time = ? where id = ?";
//            this.jdbcTemplate.update(sql, cs.getStatus(), cs.getStartKeywordSet().size(), cs.getCrawedCount(), new Timestamp(System.currentTimeMillis()), cs.getId());
//        }
    }

    ;

    public static String PROXY_TABLE = "proxy_server";

    @Override
    public Proxy getProxy(int siteId) {

        String sql = "select id, ip, port, site_interval from " + PROXY_TABLE + " where domain_id=" + siteId + " and available = 1 and site_last_used < sysdate - 1/24/60/60 order by site_last_used";
        System.out.println(sql);
        List<Proxy> proxyList = this.jdbcTemplate.query(sql, new RowMapper<Proxy>() {

            @Override
            public Proxy mapRow(ResultSet rs, int arg1) throws SQLException {
                Proxy proxy = new Proxy();
                int id = rs.getInt(1);
                HttpHost host = new HttpHost(rs.getString(2), rs.getInt(3));
                proxy.setId(id);
                proxy.sethHost(host);
                return proxy;
            }

        });

        if (proxyList != null && proxyList.size() != 0) return proxyList.get(0);

        return null;

    }

    public void updateProxyOrder(String proxy_info) {
        proxy_info = proxy_info.replace("http://", "");
        String ip = proxy_info.split(":")[0];
        String port = proxy_info.split(":")[1];
        String domainId = proxy_info.split(":")[2];
        String sql = "update " + PROXY_TABLE + " set site_last_used = ? where ip = ? and port = ? and domain_id = ?";
        this.jdbcTemplate.update(sql, new Timestamp(System.currentTimeMillis()), ip, Integer.parseInt(port), Integer.parseInt(domainId));
        Systemconfig.sysLog.log("proxy:{" + proxy_info + "} last used updated.");
    }


    public static String HEADER_TABLE = "headers";

    @Override
    public Header randomHeaderFromDB() {
        String sql = "select id, cookie ,user_agent, host, accept, accept_language, accept_encoding, connection, cache_control, referer " + "from " + HEADER_TABLE + " where site_name = 'sogou'  and cookie not like '%-1%' and trunc(sysdate)-trunc(insert_time)<7 and cookie is not null";
        System.out.println(sql);
        List<Header> headers = this.jdbcTemplate.query(sql, new RowMapper<Header>() {

            @Override
            public Header mapRow(ResultSet rs, int arg1) throws SQLException {

                Header header = new Header();
                header.setId(rs.getInt(1));
                header.setCookie(rs.getString(2));
                header.setUserAgent(rs.getString(3));
                header.setHost(rs.getString(4));
                header.setAccept(rs.getString(5));
                header.setAcceptLanguage(rs.getString(6));
                header.setAcceptEncoding(rs.getString(7));
                header.setConnection(rs.getString(8));
                header.setCacheControl(rs.getString(9));
                header.setReferer(rs.getString(10));

                return header;
            }
        });

        if (headers.size() > 0) {
            /* 随机返回一个 */
            Random random = new Random();
            int num = random.nextInt(headers.size());

            return headers.get(num);
        } else {
            System.err.println("no availiable header in header pool.");
            return null;
        }
    }

    @Override
    public abstract void saveData(T data);

    @Override
    public List<SearchKey> searchKeys() {


        this.jdbcTemplate.update("update search_keyword set type = ';'||type where type not like ';%'");
        this.jdbcTemplate.update("update search_keyword set type = type||';' where type not like '%;'");
        String table = "search_keyword";
        String col = "keyword";
        String sql = null;
        String clause = " where status=2";
        switch (Systemconfig.crawlerType) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
            case 13:
            case 15:
            case 17:
            case 19:
            case 21:
            case 23:
            case 25:
            case 27:
            case 29:
            case 31: {
                //person
                clause += " and type like '%;" + (Systemconfig.crawlerType + 1) / 2 + ";%' ";
                break;
            }
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14:
            case 16:
            case 18:
            case 20:
            case 28:
            case 30:
            case 32:
            case 34: {
                col = "url, site_name";
                table = "monitor_site";
                clause += " and type= " + ((Systemconfig.crawlerType + 1) % 2) + " and media_type=" + ((Systemconfig.crawlerType + 1) / 2);
                break;
            }

        }
        clause += " and (is_usable=0 or is_usable is null) ";
        if (Systemconfig.mode.equals("test")) {
            clause += " and debug ＝ 1 ";
        }
        if (Systemconfig.clientinfo != null) {
            sql = "select category_code, " + col + " from (select A." + col + ", A.category_code, rownum rn from (select distinct " + col + ", category_code " + "from " + table + clause + ") A where rownum <= " + Systemconfig.clientinfo.getDataEnd() + ") where rn >" + Systemconfig.clientinfo.getDataStart();
        } else {
            sql = "select category_code, " + col + " from " + table + clause;
        }
        System.out.println(sql);
        return this.jdbcTemplate.query(sql, new RowMapper<SearchKey>() {
            @Override
            public SearchKey mapRow(ResultSet rs, int i) throws SQLException {
                SearchKey sk = new SearchKey();
                sk.setKey(rs.getString(2));
                sk.setRole(rs.getInt(1));
                if ((Systemconfig.crawlerType + 1) % 2 == 1) {
                    sk.setSite(rs.getString(3));
                }
                return sk;
            }
        });
    }

    public int saveGongzhongData(WxpublicData wpd) {

        return 0;
    }

    ;

}
