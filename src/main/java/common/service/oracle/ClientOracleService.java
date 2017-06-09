package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import common.bean.ClientData;
import common.bean.ReplyData;
import common.system.Systemconfig;
import common.util.StringUtil;

public class ClientOracleService extends OracleService<ClientData> {


	

	private static final String COMM = "Client_data_comment";
	private static final String DATA = "Client_data";

	private static final String csql = "insert into " + COMM + "(" + "md5," + "name," + "pubtime," + "insert_time,"
			+ "content," + "img_url," + "data_id) values(?,?,?,?,?,?,?)";

	public void saveDatas(List<ReplyData> list, int id) {
		for (ReplyData vd : list)
			saveComment(vd, id);
	}

	private void saveComment(final ReplyData vd, final int refer) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(csql, new String[] { "id" });
				ps.setString(1, vd.getMd5());
				ps.setString(2, vd.getName());
				ps.setObject(3, new Timestamp(vd.getPubdate().getTime()));
				ps.setObject(4, new Timestamp(System.currentTimeMillis()));
				ps.setString(5, vd.getContent());
				ps.setString(6, vd.getImgUrl());
				ps.setInt(7, refer);
				return ps;
			}
		}, keyHolder);
	}

	/*
	 ID
TITLE
AUTHOR
SITE_ID
PUBTIME
URL
SEARCH_KEYWORD
COMMENT_COUNT
CLICK_COUNT
CATEGORY_CODE
INSERT_TIME
MD5
CONTENT
BRIEF
IMG_URL
RELIABILITY
SOURCE
OLD_ID
SOURCE_URL
DISLIKE_COUNT
LIKE_COUNT 
	 
	 */
	
	private static final String vsql = "insert into " + DATA + "(" + "url, " + "md5," + "title," + "author," + "brief,"
			+ "insert_time," + "content," + "comment_count," + "click_count," + "search_keyword," + "site_id,"
			+ "img_url," + "pubtime," + "dislike_count,"+ "like_count,"+ "source_url,"+ "category_code) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	@Override
	public void saveData(final ClientData vd) {
		// if(findId(vd.getMd5(), DATA)>0) return;

		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		try{
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(vsql, new String[] { "id" });
					ps.setString(1, 	vd.getUrl());
					ps.setString(2, 	vd.getMd5());
					ps.setString(3, 	vd.getTitle());
					ps.setString(4, 	vd.getAuthor());
					ps.setString(5, 	vd.getBrief());
					ps.setObject(6, 	new Timestamp(System.currentTimeMillis()));
					ps.setString(7, 	vd.getContent());
					ps.setInt(8, 		vd.getReplyCount());
					ps.setInt(9, 		vd.getClickCount());
					ps.setString(10, 	vd.getSearchKey()); 
					ps.setInt(11, 		vd.getSiteId());
					ps.setString(12, 	vd.getImgUrl());
					ps.setObject(13, 	new Timestamp(vd.getPubdate().getTime()));
					ps.setInt(14, 		vd.getDislikeCount());
					ps.setInt(15, 		vd.getLikeCount());
					ps.setString(16, 	vd.getSourceUrl());
					ps.setInt(17, 		vd.getCategoryCode());
					return ps;
				}           
			}, keyHolder);  
		}catch(Exception e){
			LOGGER.info("插入异常！！！"+e.getMessage());
			return;
		}
		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
		
		saveCommonData(vd);
	}

	public void saveCommonData(ClientData vd) {
		if (vd.getReplyList() != null) {
			for (ReplyData rd : vd.getReplyList()) {
				saveComment(rd, vd.getId());
			}
		}
	}
}
