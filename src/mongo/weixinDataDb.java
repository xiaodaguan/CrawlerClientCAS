package mongo;

import com.sun.xml.internal.ws.api.server.EndpointData;
import common.bean.WeixinData;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        String sql = "select id, begin_date, end_date from " + collectTable + " where status = 1 or status = 0";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        HashMap<Integer, String> tasks = new HashMap<Integer, String>();

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs == null) return null;
            while (rs.next()) {
                int id = rs.getInt(1);
                Date begin = rs.getDate(2);
                Date end = rs.getDate(3);
                tasks.put(id, sdf.format(begin) + "~" + sdf.format(end));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return tasks;
    }


    @Override
    public List<String> getCrawled(String table) {
        List<String> md5List = new ArrayList<String>();
        String sql = "select distinct(md5) from " + table;
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                md5List.add(rs.getString("md5"));
            }
            return md5List;

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
     * 是否存在于oracle中,如果是返回表中id,否则-1
     *
     * @param wd
     * @param table
     * @return
     */
    private int getOracleId(WeixinData wd, String table) {


        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select id from " + table + " where md5 = '" + wd.getMd5() + "'");
            if (rs.next()) {
                return rs.getInt("id");

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * update data
     *
     * @param wd
     * @param table
     * @param id
     * @throws SQLException
     */
    private void updateData(WeixinData wd, String table, int id) throws SQLException {
        String sql = "update " + table + " set read_num_24hours= ?, like_num_24hours = ?, update_time = ? where id = " + id;
        PreparedStatement ps = null;
        ps = conn.prepareStatement(sql);
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
    private void insertData(WeixinData wd, String table) throws SQLException {
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
    public int saveOrUpdateData(WeixinData wd, String table) {
        if (wd.getPubdate() == null) {
            if (wd.getPubtime() != null) System.out.println(wd.getTitle() + ":" + wd.getPubtime());
            return -2;
        }


        int id = getOracleId(wd, table);
        if (id >= 0) {//表中已存在

            //已采集过的记录,只更新readlike
            try {
                updateData(wd, table, id);
                logger.debug("updated id: " + id);

                return 2;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            try {
                insertData(wd, table);
                logger.debug("inserted.");
                return 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return -1;
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
//        wdb.saveOrUpdateData(wd, "test");
        wdb.getItemsToCollect("collect");
    }


}
