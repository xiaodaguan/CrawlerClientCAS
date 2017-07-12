package common.system;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * Created by guanxiaoda on 2017/7/5.
 */
public class UserManagerTest {



    @BeforeClass
    public static void beforeAll(){
        Systemconfig.crawlerType = 7;
        AppContext.initAppCtx("");
        List<UserAttr> list = Systemconfig.dbService.getLoginUsers("sina_weibo");
        if(Systemconfig.users == null)
            Systemconfig.users = new HashMap<>();
        Systemconfig.users.put("sina", list);
    }

    @Test
    public void getUserTest(){
        System.out.println(UserManager.getAllUserNames("sina"));
        for(int i=0;i<16;i++) {
            UserAttr user1 = UserManager.getUser("sina");
            if(user1!=null) {
                System.out.println(user1.getName());
                if (user1.getName().contains("3507")||user1.getName().contains("5146")) {
                    for (int j = 0; j < 6; j++)
                        UserManager.incrFailedTime("sina", user1);
                }
            }else{
                System.err.println("无可用账户");
            }
//            UserManager.releaseUser("sina",user1);
        }

        System.out.println(UserManager.getAllUserNames("sina"));
    }
}
