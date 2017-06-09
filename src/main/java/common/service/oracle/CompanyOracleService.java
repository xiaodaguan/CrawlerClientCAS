package common.service.oracle;

import common.bean.CompanyData;
import common.http.sub.TiebaHttpProcess;
import common.system.Systemconfig;
import common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CompanyOracleService extends OracleService<CompanyData> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyOracleService.class);

    private static final String TABLE = "company_data";

    private static final String jasql = "insert into " + TABLE +
            "(" +
            "address, " +
            "brief, " +
            "brief_products, " +
            "company_name, " +
            "contact, " +
            "" +
            "field, " +
            "found_date, " +
            "funding_experience, " +
            "ico, " +
            "products, " +
            "website," +
            "md5," +
            "title," +
            "url," +
            "short_name,"+
            "search_keyword"+
            ")" +
            " values" +
            "(" +
            "?,?,?,?,?," +
            "?,?,?,?,?," +
            "?,?,?,?,?," +
            "?"+
            ")";

    public void saveData(final CompanyData vd) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try{
	        this.jdbcTemplate.update(new PreparedStatementCreator() {
	            @Override
	            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
	                PreparedStatement ps = con.prepareStatement(jasql, new String[]{"id"});
	                ps.setString(1, vd.getAddress());
	                ps.setString(2, vd.getBrief());
	                ps.setString(3, vd.getBriefProducts());
	                ps.setString(4, vd.getName());
	                ps.setString(5, vd.getContact());
	//				
	                ps.setString(6, vd.getField());
	                ps.setString(7, vd.getFoundDate());
	                ps.setString(8, vd.getFundingExperience());
	                ps.setString(9, vd.getIco());
	                ps.setString(10, vd.getProducts());
	//				
	                ps.setString(11, vd.getWebsite());
	                ps.setString(12, vd.getMd5());
	                ps.setString(13, vd.getTitle());
	                ps.setString(14, vd.getUrl());
	                ps.setString(15,vd.getShortName());
	                ps.setString(16,vd.getSearchKey());
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
