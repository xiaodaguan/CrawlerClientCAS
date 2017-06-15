package common.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by guanxiaoda on 2017/6/15.
 */
public class DBServiceImpTest {



    @Before
    public void before(){


         ApplicationContext  context = new ClassPathXmlApplicationContext("app-sysconfig.xml");


    }

    @Test
    public void test(){

    }
}
