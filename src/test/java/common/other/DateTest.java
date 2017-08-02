package common.other;

import common.system.Systemconfig;
import org.junit.Test;

import java.util.Date;
import java.util.Random;

public class DateTest {


    @Test
    public void  test(){
        System.out.println((new Date()).getTime());
        System.out.println((new Date(System.currentTimeMillis())).getTime());
        System.out.println(System.currentTimeMillis());
        System.out.println(new Date().toLocaleString());
    }


    @Test
    public void random(){
        Random rand = new Random();
        for (int i=0;i<1000;++i) {
            int randNum = rand.nextInt(3);
            System.out.print(randNum+" ");
        }
    }
}
