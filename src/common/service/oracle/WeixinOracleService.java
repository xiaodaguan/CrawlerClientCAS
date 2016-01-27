package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.WeixinData;
import common.bean.WxpublicData;
import common.system.Systemconfig;
import common.util.StringUtil;

public class WeixinOracleService extends OracleService<WeixinData> {

	private static final String GONGZHONG_TABLE = "search_gongzhong";
	private static final String gongzhong_sql = "insert into " + GONGZHONG_TABLE
			+ "(name, creat_time, url, brief, account, img, pos_icon, openid, verify, md5, twoD_code, from_article, keyword_id) "
			+ "values " + "(?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public int saveGongzhongData(final WxpublicData vd) {
		Systemconfig.sysLog.log("saving: [" + vd.getName() + "]");
		// 新数据保存
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(gongzhong_sql, new String[] { "id" });
				ps.setString(1, vd.getName());
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps.setString(3, vd.getUrl());
				ps.setString(4, vd.getBrief());
				ps.setString(5, vd.getWeixinName());
				ps.setString(6, vd.getUserIcon());
				ps.setString(7, vd.getPosIcon());

				ps.setString(8, vd.getOpenId());
				ps.setString(9, vd.getVerify());

				ps.setString(10, vd.getMd5());
				ps.setString(11, vd.getPosIcon());
				ps.setInt(12, vd.getFromAriticle());
				ps.setInt(13, vd.getCustomizeId());
				return ps;
			}
		}, keyHolder);
		Systemconfig.sysLog.log(vd.getName() + "[保存]完成。。。");
		vd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
		return vd.getId();
	}

	private static final String TABLE = "weixin_data";

	private static final String jasql = "insert into " + TABLE + "(" + "title, " + "author," + "pubtime," + "source,"
			+ "url," + "inserttime," + "search_keyword," + "category_code," + "md5," + "content," + "brief,"
			+ "site_id," + "img_url," + "same_num, " + "same_url, " + "read_num, " + "like_num, "
			+ "gongzhong_id"+") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public void saveData(final WeixinData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
				ps.setString(1, vd.getTitle());
				ps.setString(2, vd.getAuthor());
				ps.setTimestamp(3,
						vd.getPubdate() == null ? new Timestamp(0) : new Timestamp(vd.getPubdate().getTime()));
				ps.setString(4, vd.getSource());
				ps.setString(5, vd.getUrl());
				ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
				ps.setString(7, vd.getSearchKey());

				ps.setInt(8, vd.getCategoryCode());// vd.getCategoryCode()
				ps.setString(9, vd.getMd5());

				ps.setString(10, vd.getContent() == null ? "null" : vd.getContent());
				ps.setString(11, vd.getBrief());
				ps.setInt(12, vd.getSiteId());
				ps.setString(13, vd.getImgUrl());
				ps.setInt(14, vd.getSamenum());
				ps.setString(15, vd.getSameUrl());
				ps.setInt(16, vd.getReadNum());
				ps.setInt(17, vd.getPraiseNum());
				ps.setInt(18,vd.getGongzhongId());
				return ps;
			}
		}, keyHolder);
		vd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}

	private static final String SAME_TABLE = "weixin_data_same";
	private static final String samesql = "insert into " + SAME_TABLE + "(" + "md5," + "title," + "source," + "url,"
			+ "insert_time," + "pubtime," + "content," + "img_url," + "data_id) values(?,?,?,?,?,?,?,?,?)";

	public void saveSameData(final WeixinData data) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(samesql, new String[] { "id" });
				ps.setString(1, data.getMd5());
				ps.setString(2, data.getTitle());
				ps.setString(3, data.getSource());
				ps.setString(4, data.getUrl());
				ps.setTimestamp(5, new Timestamp(data.getInserttime().getTime()));
				ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
				ps.setString(7, data.getContent());
				ps.setString(8, data.getImgUrl());
				ps.setInt(9, data.getId());
				return ps;
			}
		}, keyHolder);
	}

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
