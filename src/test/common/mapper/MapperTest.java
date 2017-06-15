package common.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guanxiaoda on 2017/6/14.
 */
public class MapperTest {

    private ApplicationContext context;

    @Before
    public void beforeClass() throws FileNotFoundException {
        context = new ClassPathXmlApplicationContext("app-sysconfig.xml");
    }

    @Test
    public void getAllMD5ByTypeTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("tablename","weixin_data");



        UserMapper mapper = (UserMapper)context.getBean("userMapper");
        int c = mapper.getTotalCountByType(map);
        System.out.println(c);
        Assert.assertNotEquals(0,c);
    }
}
