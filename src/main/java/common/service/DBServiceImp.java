package common.service;

import common.pojos.CommonData;
import common.pojos.Proxy;
import common.pojos.WxpublicData;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.mapper.UserMapper;
import common.system.SiteTemplateAttribute;
import common.system.Systemconfig;
import common.system.UserAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public class DBServiceImp<T> implements DBService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImp.class);

    private UserMapper mapper;

    public void setMapper(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void saveDatas(List<T> list) throws IOException {
        for (T data : list) {
            saveData(data);
        }
    }

    @Override
    public void saveData(T data) throws IOException {

    }

    @Override
    public List<String> getAllMd5(String tablename) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename",tablename);
        return mapper.getAllMD5ByType(paramMap);
    }

    @Override
    public void exceptionData(String md5, String table) {

    }

    @Override
    public List<SearchKey> searchKeys() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename","search_keyword");


        List<Object> list = mapper.getAllDataByType(paramMap);


        String crawlerTypeName = CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name();
        String dataType = crawlerTypeName.toLowerCase().substring(0,crawlerTypeName.indexOf("_")).+"Data";



        return null;
    }

    @Override
    public List<UserAttribute> getLoginUsers(String site) {
        return null;
    }

    @Override
    public Map<String, SiteTemplateAttribute> getXpathConfig() {
        return null;
    }

    @Override
    public String getTypeConfig() {
        return null;
    }

    @Override
    public void updateUserValid(String userName, int mark) {

    }

    @Override
    public void updateUserOrder(String userName) {

    }

    @Override
    public Proxy getProxy(int siteId) {
        return null;
    }

    @Override
    public void updateProxyOrder(String proxyInfo) {

    }

    @Override
    public int saveGongzhongData(WxpublicData wpd) {
        return 0;
    }

    @Override
    public void saveCommentDatas(List list) throws IOException {

    }

    @Override
    public void saveCommentData(T commentData) throws IOException {

    }

    @Override
    public List<? extends CommonData> filterDuplication(List list) {
        return null;
    }



    @Override
    public void removeDataBaseDuplication(List url, String table) {

    }

    @Override
    public int getDataCount(String tablename){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tablename",tablename);
        return mapper.getTotalCountByType(paramMap);
    }
}
