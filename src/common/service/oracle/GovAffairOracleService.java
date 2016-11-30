package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.GovAffairData;
import common.bean.NewsData;
import common.util.StringUtil;

public class GovAffairOracleService extends OracleService<GovAffairData> {


    private static final String TABLE = "govaffair_data";


    private static final String jasql = "insert into " + TABLE + "(" +
            "title, " +
            "author," +
            "pubtime," +
            "source," +
            "url," +
            "inserttime," +
            "search_keyword," +
            "category_code," +
            "md5," +
            "content," +
            "brief," +
            "site_id," +
            "img_url) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
//    AUTHOR
//    BRIEF
//    CATEGORY_CODE
//    CONTENT
//    ID
//    IMG_URL
//    INSERTTIME
//    MD5
//    OLD_ID
//    PUBTIME
//    SAME_NUM
//    SAME_URL
//    SEARCH_KEYWORD
//    SITE_ID
//    SOURCE
//    TITLE
//    URL

            public void saveData(final GovAffairData vd) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                this.jdbcTemplate.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(jasql, new String[]{"id"});
                        ps.setString(1, vd.getTitle());
                        ps.setString(2, vd.getAuthor());
                        ps.setTimestamp(3, vd.getPubdate() == null ? new Timestamp(0) : new Timestamp(vd.getPubdate().getTime()));
                        ps.setString(4, vd.getSource());
                        ps.setString(5, vd.getUrl());
                        ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
		                ps.setString(7, vd.getSearchKey());
		
		                ps.setInt(8, vd.getCategoryCode());//vd.getCategoryCode()
		                ps.setString(9, vd.getMd5());
		
		                ps.setString(10, vd.getContent() == null || vd.getContent().equals("") ? "no content." : vd.getContent());
		                ps.setString(11, vd.getBrief());
		                ps.setInt(12, vd.getSiteId());
		                ps.setString(13, vd.getImgUrl());
		             
		                return ps;
            }
        }, keyHolder);

        vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
    }

//    private static final String SAME_TABLE = "govaffair_data_same";
//    private static final String samesql = "insert into " + SAME_TABLE + "(" +
//            "md5," +
//            "title," +
//            "source," +
//            "url," +
//            "insert_time," +
//            "pubtime," +
//            "content," +
//            "img_url," +
//            "data_id) values(?,?,?,?,?,?,?,?,?)";
//
//    public void saveSameData(final NewsData data) {
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        this.jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                PreparedStatement ps = con.prepareStatement(samesql, new String[]{"id"});
//                ps.setString(1, data.getMd5());
//                ps.setString(2, data.getTitle());
//                ps.setString(3, data.getSource());
//                ps.setString(4, data.getUrl());
//                ps.setTimestamp(5, new Timestamp(data.getInserttime().getTime()));
//                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
//                ps.setString(7, data.getContent());
//                ps.setString(8, data.getImgUrl());
//                ps.setInt(9, data.getId());
//                return ps;
//            }
//        }, keyHolder);
//    }

//	private int findId(String md5, String table) {
//		String col = "id";
//		String caluse = "md5";
//		String sql = "select "+col+" from "+table+" where "+caluse+"=?";
//		int id = 0;
//		try {
//			id = this.jdbcTemplate.queryForInt(sql, new Object[]{md5});
//		} catch (Exception e) {
//			id = 0;
//		}
//		return id;
//	}


}
