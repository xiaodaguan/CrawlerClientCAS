package common.service.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.ReportData;
import common.system.Systemconfig;
import common.util.StringUtil;

public class ReportOracleService extends OracleService<ReportData> {

	private static final String TABLE = "company_report";

	private static final String jasql = "insert into "+TABLE+"(" +
			"title, " +
			"company_id," +
			"pubtime," +
			"type_id," +
			"search_key," +
			"url,"+ 
			"md5, " +
			"category_id, " +
			"insert_time) values(?,?,?,?,?,?,?,?,?)";
	public void saveData(final ReportData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try{
			this.jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(jasql, new String[]{"id"});
					ps.setString(1, vd.getTitle());
					ps.setString(2, vd.getCompanyId());
					ps.setTimestamp(3, vd.getPubdate()==null? new Timestamp(0):new Timestamp(vd.getPubdate().getTime()));
					ps.setInt(4, vd.getTypeId());
					ps.setString(5, vd.getSearchKey());
					
					ps.setString(6, vd.getUrl());//vd.getCategoryCode()
					ps.setString(7, vd.getMd5());
					ps.setInt(8, vd.getCategoryCode());
					ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
					return ps;
				}
			}, keyHolder);
		
		 }
		catch(Exception e){
			LOGGER.info("插入异常！！！"+e.getMessage());
			return;
		}
		vd.setId(Integer.parseInt(StringUtil.extractMulti(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}
	
	
}
