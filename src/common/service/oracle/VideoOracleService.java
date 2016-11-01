package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


import common.bean.VideoData;
import common.util.StringUtil;

public class VideoOracleService extends OracleService<VideoData> {

	private static final String TABLE = "soku_video_data";

	private static final String jasql = "insert into " + TABLE + "(" + "AUTHOR, " + "AUTHOR_URL," + "CHANNEL,"
			+ "COMMENT_URL," + "DISLIKE_COUNT," + "INSERT_TIME," + "LIKE_COUNT," + "MD5," + "PLAY_COUNT," + "PLAY_TIME,"
			+ "PUBTIME," + "TAGS, " + "TITLE, " + "URL) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final int AUTHOR = 1;
	private static final int AUTHOR_URL = 2;
	private static final int CHANNEL = 3;
	private static final int COMMENT_URL = 4;
	private static final int DISLIKE_COUNT = 5;
	private static final int INSERT_TIME = 6;
	private static final int LIKE_COUNT = 7;
	private static final int MD5 = 8;
	private static final int PLAY_COUNT = 9;
	private static final int PLAY_TIME = 10;
	private static final int PUBTIME = 11;
	private static final int TAGS = 12;
	private static final int TITLE = 13;
	private static final int URL = 14;
	
	@Override
	public void saveData(final VideoData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			//@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
				ps.setString(AUTHOR, 			vd.getAuthor());//1
				ps.setString(AUTHOR_URL, 		vd.getAuthorUrl());//2
				ps.setString(CHANNEL, 			vd.getChannel());//3
				ps.setString(COMMENT_URL, 		vd.getCommentUrl());//4
				ps.setInt(DISLIKE_COUNT, 		vd.getDislikeCount());//5
				ps.setTimestamp(INSERT_TIME, 	new Timestamp((new Date()).getTime()));//6
				ps.setInt(LIKE_COUNT, 			vd.getLikeCount());//7
				ps.setString(MD5, 				vd.getMd5());//8
				ps.setInt(PLAY_COUNT, 			vd.getPlayCount());//9
				ps.setString(PLAY_TIME, 		vd.getPlaytime());//10
				ps.setTimestamp(PUBTIME,							//11
												vd.getPubdate() == null ? new Timestamp(0) : 
													new Timestamp(vd.getPubdate().getTime()));
				ps.setString(TAGS, 				vd.getTags());//12
				ps.setString(TITLE, 			vd.getTitle());//13
				ps.setString(URL, 				vd.getUrl());//14
				
				System.out.println(ps.toString());
				return ps;
			}
		}, keyHolder);

		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}
}
