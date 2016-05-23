package mongo;

import java.util.HashMap;

/**
 * Created by guanxiaoda on 16/4/15.
 */
public class db<T> {

    /**
     * @return list of item['md5'] in oracle
     */
    public HashMap<String, Integer> getCrawled(String table) {return null;}

    ;

    /**
     * 读collect表,整理需要更新的记录,返回需要更新的时间段+collectIds
     *
     * @return time range to be collected. <e.g.>
     * hashmap:
     * <integer,string>
     * 1,2016-01-01 00:00:00~2016-02-02 12:12:12
     * </integer,string>
     * </e.g.>
     * null if nothing to be collected.
     */
    public HashMap<Integer, String> getItemsToCollect(String collectTable) {
        return null;
    }

    /**
     * 更新collect任务状态
     * @param collectId
     * @return true: 成功
     */
    public boolean updateCollectStatus(String collectTable, int collectId) {return false;}


}
