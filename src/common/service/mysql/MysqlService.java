package common.service.mysql;

import common.bean.*;
import common.rmi.packet.SearchKey;
import common.service.AbstractDBService;
import common.system.Systemconfig;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * 数据库操作
 * @author grs
 *
 */
public abstract class MysqlService<T> extends AbstractDBService<T> {
	@Override
	public void saveCommentData(T t) throws IOException {

	}
	public void updateProxyOrder(String proxyInfo) {
	}
	public int saveGongzhongData(WxpublicData wpd) {
		return 0;

	};
	

	@Override
	public Proxy getProxy(int siteId){
		return null;
	}
	/**
	 * 随机返回一个header
	 */
	@Override
	public Header randomHeaderFromDB() {
		String sql = "select id, cookie ,user_agent, host, accept, accept_language, accept_encoding, connection, cache_control, referer "
				+ "from headers where site_name = 'sogou' and cookie not like '%-1%' and to_days(now())-to_days(insert_time)<7 and cookie is not null limit 200";
		List<Header> headers = this.jdbcTemplate.query(sql, new RowMapper<Header>() {

			@Override
			public Header mapRow(ResultSet rs, int arg1) throws SQLException {

				Header header = new Header();
				header.setId(rs.getInt(1));
				header.setCookie(rs.getString(2));
				header.setUserAgent(rs.getString(3));
				header.setHost(rs.getString(4));
				header.setAccept(rs.getString(5));
				header.setAcceptLanguage(rs.getString(6));
				header.setAcceptEncoding(rs.getString(7));
				header.setConnection(rs.getString(8));
				header.setCacheControl(rs.getString(9));
				header.setReferer(rs.getString(10));

				return header;
			}
		});

		if (headers.size() > 0) {
			/* 随机返回一个 */
			Random random = new Random();
			int num = random.nextInt(headers.size());

			return headers.get(num);
		} else {
			System.err.println("no availiable header in header pool.");
			return null;
		}
	}
	
	@Override
	public abstract void saveData(T data);
	@Override
	public List<SearchKey> searchKeys() {
		String table = null;
		String col = null;
		switch(Systemconfig.crawlerType) {
		case 1 : 
		case 3 :
		case 5 :
		case 7 :
		case 9 : 
		case 11 :
		case 13 :{
			col = "keyword";
			table = "search_keyword";
		}
		case 2 :
		case 4 :
		case 6 :
		case 8 :
		case 10 : 
		case 12 : 
		case 14 : {
			col = "url";
			table = "monitor_site";
		}
		}
		String sql = null;
		if(Systemconfig.clientinfo != null) {
			sql = "select distinct "+col+", category_code from "+table+" where status=2 limit "+ 
					Systemconfig.clientinfo.getDataStart()+", "+Systemconfig.clientinfo.getDataEnd();
		} else {
			sql = "select distinct "+col+", category_code from "+table+" where status=2";
		}
		
		return this.jdbcTemplate.query(sql, new RowMapper<SearchKey>(){
			@Override
			public SearchKey mapRow(ResultSet rs, int i)
					throws SQLException {
				SearchKey sk = new SearchKey();
				sk.setKey(rs.getString(1));
				sk.setRole(rs.getInt(2));
				return sk;
			}
		});
	}
	
}
