package mongo;

import common.bean.WeixinData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxiaoda on 16/4/15.
 */
public class weixinDataDb extends db<WeixinData> {
    Logger logger = LoggerFactory.getLogger(weixinDataDb.class);
    private Connection conn = null;
    private String URL = "jdbc:oracle:thin:@192.168.1.103:1521/ORCL";
    private String USERNAME = "qdtramgr";
    private String PASSWORD = "qdtramgr";
    private String TABLE = "weixin_data";

    public weixinDataDb() {
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
    public List<String> getCrawled(){
        List<String> md5List = new ArrayList<String>();
        String sql = "select distinct(md5) from "+TABLE;
        Statement st= null;
        ResultSet rs = null;
        try {
             st = conn.createStatement();
             rs = st.executeQuery(sql);
            while(rs.next()){
                md5List.add(rs.getString("md5"));
            }
            return md5List;

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                st.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        return null;
    }

    @Override
    public int saveData(WeixinData wd) {
        if (wd.getPubdate()==null){
            if(wd.getPubtime()!=null)
                System.out.println(wd.getTitle()+":"+wd.getPubtime());
            return -2;
        }





        String sql = "insert into " + TABLE + "(" +
                "title, author, pubtime, url, search_keyword, " +
                "source, category_code, inserttime, md5, content, " +
                "brief, img_url, read_num, like_num" +
                ") values(" +
                "?,?,?,?,?," +
                "?,?,?,?,?," +
                "?,?,?,?" +
                ")";
//        String sql = "insert into " + TABLE + "(title) values(?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);

            ps.setString(1, wd.getTitle());
            ps.setString(2, wd.getAuthor());
            ps.setTimestamp(3, new Timestamp(wd.getPubdate().getTime()));
            ps.setString(4, wd.getUrl());
            ps.setString(5, wd.getSearchKey());

            ps.setString(6, wd.getSource());
            ps.setInt(7, wd.getCategoryCode());
            if (wd.getInserttime()!=null)
            ps.setTimestamp(8, new Timestamp(wd.getInserttime().getTime()));
            else
            ps.setTimestamp(8,new Timestamp(System.currentTimeMillis()));
            ps.setString(9, wd.getMd5());
            ps.setString(10, wd.getContent());

            ps.setString(11, wd.getBrief());
            ps.setString(12, wd.getImgUrl());
            ps.setInt(13, wd.getReadNum());
            ps.setInt(14, wd.getPraiseNum());

            ps.execute();
            int count = ps.getUpdateCount();

//            logger.info("weixindata:[{}] wrote into oracle: [{}]", wd.getTitle(), URL + "/" + TABLE);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }


    public static void main(String[] args) {
        WeixinData wd = new WeixinData();
        wd.setTitle("test");
        weixinDataDb wdb = new weixinDataDb();
        wdb.saveData(wd);
    }
}
