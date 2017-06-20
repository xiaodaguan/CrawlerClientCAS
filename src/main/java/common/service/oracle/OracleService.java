package common.service.oracle;

import common.bean.*;
import common.rmi.packet.SearchKey;
import common.service.AbstractDBService;
import common.system.Systemconfig;


import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * 数据库操作
 *
 * @author grs
 */
public abstract class OracleService<T> extends AbstractDBService<T> {
    @Override
    public void saveCommentData(T t) throws IOException {

    }


    @Override
    public abstract void saveData(T data);

    @Override
    public List<SearchKey> searchKeys() {


        this.jdbcTemplate.update("update search_keyword set type = ';'||type where type not like ';%'");
        this.jdbcTemplate.update("update search_keyword set type = type||';' where type not like '%;'");
        String table = "search_keyword";
        String col = "keyword";
        String sql = null;
        String clause = " where status=2 ";
        switch (Systemconfig.crawlerType) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 11:
            case 13:
            case 15:
            case 17:
            case 19:
            case 21:
            case 23:
            case 25:
            case 27:
            case 29:
            case 31: 
            case 37:
            case 39:
            case 41:
            case 43:
            case 45:{
                //person
                clause += " and type like '%;" + (Systemconfig.crawlerType + 1) / 2 + ";%' ";
                break;
            }
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14:
            case 16:
            case 18:
            case 20:
            case 28:
            case 30:
            case 32:
            case 34:
            case 38:
            case 40:
            case 42:
            case 46:{
                col = "url, site_name";
                table = "monitor_site";
                clause += " and type= " + ((Systemconfig.crawlerType + 1) % 2) + " and media_type=" + ((Systemconfig.crawlerType + 1) / 2);
                break;
            }	
           
        }
        clause += " and (is_usable=0 or is_usable is null)";
        if (Systemconfig.mode.equals("test")) {
            clause += " and debug ＝ 1 ";
        }
        clause += " order by priority desc ";
        if (Systemconfig.clientinfo != null) {
            sql = "select category_code, " + col + " from (select A." + col + ", A.category_code, rownum rn from (select distinct " + col + ", category_code " + "from " + table + clause + ") A where rownum <= " + Systemconfig.clientinfo.getDataEnd() + ") where rn >" + Systemconfig.clientinfo.getDataStart();
        } else {
            sql = "select category_code, " + col + " from " + table + clause;
        }
        
        if(Systemconfig.crawlerCount!=0&&Systemconfig.crawlerNum!=0){
        	
        	String choiceKeyword = " mod(id,"+Systemconfig.crawlerCount+") = "+ (Systemconfig.crawlerNum-1);
            sql = sql.replace("where","where "+choiceKeyword+" and ");
        }
        
        try {
        	Connection con = this.jdbcTemplate.getDataSource().getConnection();
            String connInfo =  con.toString();
            if(connInfo.toLowerCase().contains("topsearch")){
                sql = sql.replace("where","where trunc(propose_time) >= trunc(sysdate) - 7 and ");
                sql = sql + " , propose_time  desc ";
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Systemconfig.sysLog.log("read searchkeys:\n"+sql);

        return this.jdbcTemplate.query(sql, new RowMapper<SearchKey>() {
            @Override
            public SearchKey mapRow(ResultSet rs, int i) throws SQLException {
                SearchKey sk = new SearchKey();
                sk.setKey(rs.getString(2));
                sk.setRole(rs.getInt(1));
                if ((Systemconfig.crawlerType + 1) % 2 == 1) {
                    sk.setSite(rs.getString(3));
                }
                return sk;
            }
        });
    }

    public int saveGongzhongData(WxpublicData wpd) {

        return 0;
    }

    ;

}
