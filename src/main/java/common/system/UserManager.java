package common.system;

import java.util.ArrayList;
import java.util.List;

import common.utils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户管理
 *
 * @author grs
 */
public class UserManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);

    /**
     * 每个用户只能运行一种类型的采集
     *
     * @return
     */
    public synchronized static UserAttribute getUser(String siteFlag) {

        List<UserAttribute> list = Systemconfig.users.get(siteFlag);
        if (list == null) return null;
        for (UserAttribute ua : list) {
            if (ua.getUsed() > 0) continue;
            ua.setUsed(1);
            return ua;
        }
        return null;
    }

    public static List<String> getAllUserNames(String siteFlag){
        List<String> list = new ArrayList<>(Systemconfig.users.size());
        for (UserAttribute attr : Systemconfig.users.get(siteFlag)) {
            list.add(attr.getName());
        }
        return list;
    }

    public static List<String> getAvailableUserNames(String siteFlag) {
        List<String> list = new ArrayList<>(Systemconfig.users.size());
        for (UserAttribute attr : Systemconfig.users.get(siteFlag)) {
            if (attr.getUsed() > 0) continue;
            else list.add(attr.getName());
        }
        return list;
    }

    public synchronized static void releaseUser(String siteFlag, UserAttribute user) {
//		int in = siteFlag.indexOf("_");
//		if(in==-1) in = siteFlag.length();
//		String typeConf = siteFlag.substring(0, in);
        if (user == null) return;
        List<UserAttribute> list = Systemconfig.users.get(siteFlag);
        for (UserAttribute ua : list) {
            if (ua.equals(user)) {
                ua.setUsed(0);
                UserAgent.releaseUserAgent(ua.getId());
            }
        }
        LOGGER.info("用户" + user.getName() + "释放.");
    }

}
