package common.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {


    @Test
    public void testFullMatch(){
        String pattern = "[http://]*\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+";


        String target1 = "172.18.79.32:8888";
        String target2 = "172.18.79.a32:8888";
        String target3 = "http://172.18.79.32:8888";
        String target4 = "http://18.79.32:8888";

        boolean result1 = StringUtil.regFullMatch(target1,pattern);
        Assert.assertTrue(result1);

        boolean result2 = StringUtil.regFullMatch(target2,pattern);
        Assert.assertFalse(result2);

        boolean result3 = StringUtil.regFullMatch(target3,pattern);
        Assert.assertTrue(result3);

        boolean result4 = StringUtil.regFullMatch(target4,pattern);
        Assert.assertFalse(result4);
    }
}
