package common.http;

import org.junit.Test;

/**
 * Created by guanxiaoda on 2017/6/20.
 */
public class SimpleHttpProcessTest {

    private static  SimpleHttpProcess shp = new SimpleHttpProcess();
    @Test
    public void testRandomUA(){
        System.out.println(shp.getRandomUserAgent());
    }
}
