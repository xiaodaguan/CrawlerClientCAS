package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.PersonData;
import common.system.Systemconfig;
import common.util.StringUtil;

public class PersonOracleService extends OracleService<PersonData> {

	private static final String TABLE = "leaders";

	private static final String jasql = "insert into " + TABLE + "(" + "name, " + "company," + "position," + "create_time," + "image," + "sex," + "brief," + "nation," + "native_place," + "birthday,"
			+ "political_status," + "school," + "education_background," + "profession," + "interest," + "education_history," + "work_experience," + "search_keyword," + "major," + "url, " + "md5, "
			+ "category_id) " + "values(" + "?,?,?,?,?," + "?,?,?,?,?," + "?,?,?,?,?," + "?,?,?,?,?," + "?,?" + ")";

	private static final String categorySql = "insert into category_scheme(name,nodelevel,code,show_order) values(?,?,?,?)";

	public void saveData(final PersonData vd) {
		int category_code = 0;
		try {
			category_code = this.jdbcTemplate.queryForInt("select id from category_scheme where name = ?", vd.getName());
		} catch (EmptyResultDataAccessException e) {
			category_code = 0;
		}
		if (category_code == 0) {
			KeyHolder keyHolder1 = new GeneratedKeyHolder();
			this.jdbcTemplate.update(new PreparedStatementCreator() {

				@Override public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(categorySql, new String[] { "id" });
					ps.setString(1, vd.getName());
					ps.setInt(2, 1);
					ps.setInt(3, 14);
					ps.setInt(4, 0);

					return ps;
				}
			}, keyHolder1);
			category_code = Integer.parseInt(StringUtil.extractMulti(keyHolder1.getKeyList().get(0).toString(), "\\d"));
		}
		vd.setCategoryCode(category_code);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try{
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				@Override public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
					ps.setString(1, vd.getName());
					ps.setString(2, vd.getCompany());
					ps.setString(3, vd.getPosition());
					ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
					ps.setString(5, vd.getImgUrl());
					ps.setInt(6, vd.getSex().contains("男") ? 1 : 2);
					ps.setString(7, vd.getBrief());
					ps.setString(8, vd.getNation());
					ps.setString(9, vd.getNativePlace());
					ps.setTimestamp(10, vd.getBirthday() == null ? null : new Timestamp(vd.getBirthday().getTime()));
					// ps.setTimestamp(10, new
					// Timestamp(System.currentTimeMillis()));
					ps.setString(11, vd.getPoliticalStatus());
					ps.setString(12, vd.getSchool());
					ps.setString(13, vd.getEducationBackground());
					ps.setString(14, vd.getProfession());
					ps.setString(15, vd.getInterest());
					ps.setString(16, vd.getEducationHistory());
					ps.setString(17, vd.getWorkExperience());
					ps.setString(18, vd.getSearchKey());
					ps.setString(19, vd.getMajor());
					ps.setString(20, vd.getUrl());
					ps.setString(21, vd.getMd5());
					ps.setInt(22, vd.getCategoryCode());
					return ps;
				}
			}, keyHolder);
		 }
		catch(Exception e){
			Systemconfig.sysLog.log("插入异常！！！"+e.getMessage());
			return;
		}
		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}

}
