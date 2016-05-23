package mongo;

import common.bean.WeixinData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by guanxiaoda on 16/4/15.
 */
public class weixinDataDb extends db<WeixinData> {
    Logger logger = LoggerFactory.getLogger(weixinDataDb.class);
    private Connection conn = null;
    private String URL = null;
    private String USERNAME = null;
    private String PASSWORD = null;

    public weixinDataDb(String url, String uname, String passwd) {
        URL = url;
        USERNAME = uname;
        PASSWORD = passwd;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

            this.conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return USERNAME + "@" + URL;
    }

    @Override
    public HashMap<Integer, String> getItemsToCollect(String collectTable) {
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "select id, begin_date, end_date from " + collectTable + " where status = 1 or status = 0 order by id desc";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<Integer, String> tasks = new HashMap<Integer, String>();

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs == null) return null;
            while (rs.next()) {
                int id = rs.getInt(1);
                Date begin = rs.getDate(2);
                Date end = rs.getDate(3);
                if (begin.getTime() == end.getTime()) {
                    begin = dateIncreaseHours(begin, 0);
                    end = dateIncreaseHours(end, 24);
                }
                tasks.put(id, sdf.format(begin) + "~" + sdf.format(end));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return tasks;
    }

    private Date dateIncreaseHours(Date begin, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.HOUR, hours);
        begin = new Date(cal.getTimeInMillis());
        return begin;
    }


    @Override
    public HashMap<String, Integer> getCrawled(String table) {
        HashMap<String, Integer> md5Map = new HashMap<String, Integer>();
        String sql = "select id,md5 from " + table;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                md5Map.put(rs.getString(2), rs.getInt(1));
            }
            return md5Map;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        return null;
    }


    /**
     * update data
     *
     * @param wd
     * @param table
     * @throws SQLException
     */
    public void updateData(WeixinData wd, String table, int id) throws SQLException {
        String sql = "update " + table + " set read_num_24hours= ?, like_num_24hours = ?, update_time = ? where id = " + id;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, wd.getReadNum());
        ps.setInt(2, wd.getPraiseNum());
        ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        ps.execute();
        ps.close();
    }

    /**
     * insert data
     *
     * @param wd
     * @param table
     */
    public void insertData(WeixinData wd, String table) throws SQLException {
        String sql = "insert into " + table + "(" +
                "title, author, pubtime, url, search_keyword, " +
                "source, category1, create_time, md5, content, " +
                "brief, ";
        sql += table.equals("article") ? "img, " : "img_url, ";
        sql += "read_num, like_num, gongzhong_id, " +
                "read_num_24hours, like_num_24hours, update_time" +
                ") values(" +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?" +
                ")";


        PreparedStatement ps = null;

        ps = conn.prepareStatement(sql);

        ps.setString(1, wd.getTitle());
        ps.setString(2, wd.getAuthor());
        ps.setTimestamp(3, new Timestamp(wd.getPubdate().getTime()));
        ps.setString(4, wd.getUrl());
        ps.setString(5, wd.getSearchKey());

        ps.setString(6, wd.getSource());
        ps.setInt(7, wd.getCategoryCode());
        if (wd.getInserttime() != null) ps.setTimestamp(8, new Timestamp(wd.getInserttime().getTime()));
        else ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
        ps.setString(9, wd.getMd5());
        ps.setString(10, wd.getContent());

        ps.setString(11, wd.getBrief());
        ps.setString(12, wd.getImgUrl());
        ps.setInt(13, wd.getReadNum());
        ps.setInt(14, wd.getPraiseNum());
        ps.setInt(15, wd.getGongzhongId());
        ps.setInt(16, wd.getReadNum());
        ps.setInt(17, wd.getPraiseNum());
        ps.setTimestamp(18, new Timestamp(wd.getInserttime().getTime()));

        ps.execute();
        ps.close();


    }


    @Override
    public boolean updateCollectStatus(String collectTable, int collectId) {

        String sql = "update " + collectTable + " set status = 2 where id = " + collectId;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;

    }

    public static void main(String[] args) {

        String URL = "jdbc:oracle:thin:@172.18.79.3:1521/ORCL";
        String USERNAME = "ashi";
        String PASSWORD = "ashi";

//        WeixinData wd = new WeixinData();
//        wd.setTitle("test");
        weixinDataDb wdb = new weixinDataDb(URL, USERNAME, PASSWORD);
        wdb.getItemsToCollect("collect");
    }


}
