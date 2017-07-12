package common.system;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import common.system.Systemconfig;
import common.util.UserAgent;

/**
 * 用户管理
 *
 * @author grs
 */
public class UserManager {

    /**
     * 每个用户只能运行一种类型的采集
     *
     * @return
     */
    public synchronized static UserAttr getUser(String siteFlag) {
//		int in = siteFlag.indexOf("_");
//		if(in==-1) in = siteFlag.length();
//		String site = siteFlag.substring(0, in);
        List<UserAttr> list = Systemconfig.users.get(siteFlag);
        if (list == null) return null;
        for (UserAttr ua : list) {
            if (ua.getUsed() > 0) continue;
            ua.setUsed(1);
            return ua;
        }
        return null;
    }

    public static List<String> getAllUserNames(String siteFlag) {
        List<String> list = new ArrayList<>(Systemconfig.users.size());
        for (UserAttr attr : Systemconfig.users.get(siteFlag)) {
            list.add(attr.getName());
        }
        return list;
    }

    public static List<String> getAvailableUserNames(String siteFlag) {
        List<String> list = new ArrayList<>(Systemconfig.users.size());
        for (UserAttr attr : Systemconfig.users.get(siteFlag)) {
            if (attr.getUsed() > 0) continue;
            else list.add(attr.getName());
        }
        return list;
    }

    public synchronized static void releaseUser(String siteFlag, UserAttr user) {
//		int in = siteFlag.indexOf("_");
//		if(in==-1) in = siteFlag.length();
//		String site = siteFlag.substring(0, in);
        if (user == null) return;
        List<UserAttr> list = Systemconfig.users.get(siteFlag);
        for (UserAttr ua : list) {
            if (ua.equals(user)) {
                ua.setUsed(0);
                UserAgent.releaseUserAgent(ua.getId());
            }
        }
        Systemconfig.sysLog.log("用户" + user.getName() + "释放.");
    }

    private static ConcurrentHashMap<String, Integer> userFailedTimes = new ConcurrentHashMap<>();

    public synchronized static void incrFailedTime(String siteFlag, UserAttr user) {
        String key = siteFlag + ":" + user.getName();
        if (!userFailedTimes.containsKey(key))
            userFailedTimes.put(key, 1);
        else {
            int curr = userFailedTimes.get(key);
            userFailedTimes.put(key, curr + 1);
        }

        if (userFailedTimes.get(key) >= 5) {
            deleteUser(siteFlag, user);
        }
    }

    public synchronized static void deleteUser(String siteFlag, UserAttr user) {

        Systemconfig.users.get(siteFlag).remove(user);
    }

}
