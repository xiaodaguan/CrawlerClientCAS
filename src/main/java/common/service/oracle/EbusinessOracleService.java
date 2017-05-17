package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.CommentData;
import common.bean.EbusinessData;
import common.bean.OwnerData;
import common.system.Systemconfig;
import common.util.StringUtil;

public class EbusinessOracleService extends OracleService<EbusinessData> {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final String oracle_product_table = "eb_data";
	private static final String oracle_comment_table = "eb_comment";
	private static final String oracle_owner_table = "eb_owner";
	private static final String jasql = "insert into " + oracle_product_table + "(" + "title, " + "brand," + "content,"
			+ "product_img," + "info_img," +

			"insert_time," + "diameter," + "width," + "price," + "sale_num," +

			"name," + "url," + "info, " + "category_code," + "md5,"

			+ "search_keyword," + "site_id," + "year_month," + "owner," + "model,"

			+ "code_num," + "company" + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String ownersql = "insert into " + oracle_owner_table + " " + "("
			+ "company, address, name, url, pscore, sscore, ascore, code_num, inserttime, searchkey, product_url" + ") "
			+ "values " + "(?,?,?,?,?,?,?,?,?,?,?)";
	public void saveData(final EbusinessData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try{
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
					ps.setString(1, vd.getTitle());
					ps.setString(2, vd.getBrand());
					ps.setString(3, vd.getContent() == null ? "" : vd.getContent());
					ps.setString(4, vd.getImgs_product());
					ps.setString(5, vd.getImgs_info());
	
					ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
					ps.setString(7, vd.getParams_diameter());
					ps.setString(8, vd.getParams_width());
					ps.setString(9, vd.getPrice());
					ps.setString(10, vd.getTransation());
	
					ps.setString(11, vd.getName());
					ps.setString(12, vd.getUrl());
					ps.setString(13, vd.getParams_params());
					ps.setInt(14, vd.getCategoryCode());
					ps.setString(15, vd.getMd5());
	
					ps.setString(16, vd.getSearchKey());
					ps.setInt(17, vd.getSiteId());
					ps.setString(18, vd.getUpdateDate());
					ps.setLong(19, Long.parseLong(vd.getOwner().getOwner_code()));
					ps.setString(20, vd.getParams_model());
	
					ps.setString(21, vd.getInfo_code());
					ps.setString(22, vd.getCompany());
					return ps;
				}
			}, keyHolder);
		}
		catch(Exception e){
			Systemconfig.sysLog.log("插入异常！！！"+e.getMessage());
			return;
		}
		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));

	}/**
	 * @param od
	 * @param args
	 *            : 0: searchkey, 1: product url
	 */
	public void saveOwner(final OwnerData od, final String... args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(ownersql, new String[] { "id" });
				ps.setString(1, od.getOwner_company());
				ps.setString(2, od.getOwner_address());
				ps.setString(3, od.getOwner_name());
				ps.setString(4, od.getOwner_url());
				ps.setString(5, od.getOwner_pScore());
				ps.setString(6, od.getOwner_sScore());
				ps.setString(7, od.getOwner_score());
				ps.setLong(8, Long.parseLong(od.getOwner_code()));
				ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
				ps.setString(10, args[0]);
				ps.setString(11, args[1]);
				return ps;
			}
		}, keyHolder);
		od.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));

	}

	private static final String commentsql = "insert into "
			+ oracle_comment_table
			+ " "
			+ "("
			+ "info, label, lv, person, product_code, product_title, pubtime, score, inserttime, cid, searchkey, product_url"
			+ ")" + "values " + "(?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * @param cd
	 * @param args
	 *            : 0: product code 1: searchkey 2: product url
	 */
	public void saveComment(final CommentData cd, final String... args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(commentsql, new String[] { "id" });
				// info, label, lv, person, product_code, product_title,
				// pubtime, score
				ps.setString(1, cd.getComment_info());
				ps.setString(2, cd.getComment_label());
				ps.setString(3, cd.getComment_level());
				ps.setString(4, cd.getComment_person());
				ps.setLong(5, Long.parseLong(args[0]));
				ps.setString(6, cd.getComment_product());
				try {
					Date date = sdf.parse(cd.getComment_pubtime().replace("年", "-").replace("月", "-").replace("日", ""));
					ps.setTimestamp(7, new Timestamp(date.getTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				ps.setString(8, cd.getComment_score());
				ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
				ps.setString(10, cd.getComment_id());
				ps.setString(11, args[1]);
				ps.setString(12, args[2]);
				return ps;
			}
		}, keyHolder);

		cd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}
	
}
