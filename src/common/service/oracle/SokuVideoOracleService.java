package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.SokuVideoData;
import common.util.StringUtil;

public class SokuVideoOracleService extends OracleService<SokuVideoData> {

	private static final String TABLE = "soku_video_data";

	private static final String jasql = "insert into " + TABLE + "(" + "AUTHOR, " + "AUTHOR_URL," + "CHANNEL,"
			+ "COMMENT_URL," + "DISLIKE_COUNT," + "INSERT_TIME," + "LIKE_COUNT," + "MD5," + "PLAY_COUNT," + "PLAY_TIME,"
			+ "PUBTIME," + "TAGS, " + "TITLE, " + "URL) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static int AUTHOR = 1;
	private static int AUTHOR_URL = 2;
	private static int CHANNEL = 3;
	private static int COMMENT_URL = 4;
	private static int DISLIKE_COUNT = 5;
	private static int INSERT_TIME = 6;
	private static int LIKE_COUNT = 7;
	private static int MD5 = 8;
	private static int PLAY_COUNT = 9;
	private static int PLAY_TIME = 10;
	private static int PUBTIME = 11;
	private static int TAGS = 12;
	private static int TITLE = 13;
	private static int URL = 14;

	public void saveData(final SokuVideoData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
				ps.setString(AUTHOR, vd.getAuthor());//1
				ps.setString(AUTHOR_URL, vd.getAuthorUrl());//2
				ps.setString(CHANNEL, vd.getChannel());//3
				ps.setString(COMMENT_URL, vd.getChannel());//4
				ps.setInt(DISLIKE_COUNT, vd.getDislikeCount());//5
				ps.setTimestamp(INSERT_TIME, new Timestamp((new Date()).getTime()));//6
				ps.setInt(LIKE_COUNT, vd.getLikeCount());//7
				ps.setString(MD5, vd.getMd5());//8
				ps.setInt(PLAY_COUNT, vd.getPlayCount());//9
				ps.setString(PLAY_TIME, vd.getPlaytime());//10
				ps.setTimestamp(PUBTIME,//11
						vd.getPubdate() == null ? new Timestamp(0) : 
							new Timestamp(vd.getPubdate().getTime()));

				ps.setString(TAGS, vd.getTags());//12
				ps.setString(TITLE, vd.getTitle());//13
				ps.setString(URL, vd.getUrl());//14
				return ps;
			}
		}, keyHolder);

		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}

	// private static final String SAME_TABLE = "news_data_same";
	// private static final String samesql = "insert into " + SAME_TABLE + "(" +
	// "md5," +
	// "title," +
	// "source," +
	// "url," +
	// "insert_time," +
	// "pubtime," +
	// "content," +
	// "img_url," +
	// "data_id) values(?,?,?,?,?,?,?,?,?)";
	//
	// public void saveSameData(final NewsData data) {
	// KeyHolder keyHolder = new GeneratedKeyHolder();
	// this.jdbcTemplate.update(new PreparedStatementCreator() {
	// @Override
	// public PreparedStatement createPreparedStatement(Connection con) throws
	// SQLException {
	// PreparedStatement ps = con.prepareStatement(samesql, new String[]{"id"});
	// ps.setString(1, data.getMd5());
	// ps.setString(2, data.getTitle());
	// ps.setString(3, data.getSource());
	// ps.setString(4, data.getUrl());
	// ps.setTimestamp(5, new Timestamp(data.getInserttime().getTime()));
	// ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
	// ps.setString(7, data.getContent());
	// ps.setString(8, data.getImgUrl());
	// ps.setInt(9, data.getId());
	// return ps;
	// }
	// }, keyHolder);
	// }

	// private int findId(String md5, String table) {
	// String col = "id";
	// String caluse = "md5";
	// String sql = "select "+col+" from "+table+" where "+caluse+"=?";
	// int id = 0;
	// try {
	// id = this.jdbcTemplate.queryForInt(sql, new Object[]{md5});
	// } catch (Exception e) {
	// id = 0;
	// }
	// return id;
	// }

}
