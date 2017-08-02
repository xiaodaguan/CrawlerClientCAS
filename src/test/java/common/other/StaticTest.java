package common.other;



class Test{
    private static String aa;
    public String getAa(){
        return this.aa;
    }
    public void setAa(String aa){
        this.aa=aa;
    }
}
public class StaticTest {

    @org.junit.Test
    public void staticTest(){
        Test test01 = new Test();
        Test test02 = new Test();
        test01.setAa("123123");
        System.out.println(test02.getAa());
    }
}
