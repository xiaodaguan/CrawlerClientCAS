package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.AgricaltureData;
import common.util.StringUtil;

public class AgricaltureOracleService extends OracleService<AgricaltureData> {

	private static final String TABLE = "agricalture_data";

	private static final String jasql = "insert into " + TABLE + ""
			+ "("
			+ "name, province, city, district, street, pubtime, source, categoryid, high_price, low_price, aver_price, unit, spec, md5, search_keyword"
			+ ")"
//			
			+ " values"
			+ "("
			+ "?,?,?,?,?"
			+",?,?,?,?,?"
			+",?,?,?,?,?"
			+ ")"
			+ "";

	public void saveData(final AgricaltureData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
				ps.setString(1, vd.getName());
				ps.setString(2, vd.getProvince());
				ps.setString(3, vd.getCity());
				ps.setString(4, vd.getDistrict());
				ps.setString(5, vd.getStreet());
//				
				ps.setTimestamp(6, new Timestamp(vd.getPubdate().getTime()));
				ps.setString(7, vd.getSource());
				ps.setInt(8, vd.getCategoryId());
				ps.setString(9, vd.getHighPrice());
				ps.setString(10, vd.getLowPrice());
//				
				ps.setString(11, vd.getAveragePrice());
				ps.setString(12, vd.getUnit());
				ps.setString(13, vd.getSpec());
				ps.setString(14, vd.getMd5());
				ps.setString(15, vd.getSearchKey());
				return ps;
			}
		}, keyHolder);

		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
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
