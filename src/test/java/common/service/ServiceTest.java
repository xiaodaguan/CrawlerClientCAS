package common.service;

import common.pojos.NewsData;
import common.task.CrawlerType;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * Created by guanxiaoda on 2017/6/19.
 */
public class ServiceTest {

    private static DBService dbService;

    @BeforeClass
    public static void before(){
        Systemconfig.crawlerType = 1;

        String crawlerTypeName = CrawlerType.getCrawlerTypeMap().get(Systemconfig.crawlerType).name().toLowerCase();
        String typeName = crawlerTypeName.substring(0, crawlerTypeName.indexOf("_"));
        String serviceName = typeName+"Service";


        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
        dbService = (DBService) AppContext.appContext.getBean(serviceName);
    }

    @Test
    public void createTest() throws Exception {
        NewsData data = new NewsData();
        data.setTitle("简书：Java反射——获取Class对象");
        data.setUrl("http://www.jianshu.com/p/efaa8567fce6");
        data.setContent("在Java语言中，除了通过new关键字来创建类对象的实例，还可以通过反射技术来创建类对象的实例。\n" +
                "  通过反射来创建类对象的实例，首先我们得拿到类对象的Class，如何获取到Class，请参考以下内容：\n" +
                "  简书：Java反射——获取Class对象\n" +
                "  在拿到类对象的Class后，就可以通过Java的反射机制来创建类对象的实例对象了，主要分为以下几种方式：\n" +
                "  Class.newInstance()\n" +
                "调用类对象的构造方法测试类\n" +
                "  首先，准备测试类如下：/** * @author likly * @version 1.0 */public class Person { private String name; private int age; public Person() { } public Person(String name, int age) { this.name = name; this.age = age; } public String getName() { return name; } public void setName(String name) { this.name = name; } public int getAge() { return age; } public void setAge(int age) { this.age = age; } @Override public String toString() { return \"Person{\" + \"name='\" + name + '\\'' + \", age=\" + age + '}'; }}Class.newInstance()\n" +
                "  测试代码如下：public class ClassNewInstance { public static void main(String[] args) throws IllegalAccessException, InstantiationException { Person person = Person.class.newInstance(); person.setAge(18); person.setName(\"likly\"); System.out.println(person); }}\n" +
                "  运行结果为：Person{name='likly', age=18}\n" +
                "  可见，通过Class.newInstance()方法是可以创建类对象的实例对象的，但是一定要注意，如果把Person的空参的构造函数去掉，并创建非空的构造函数，再次运行上面的代码，将会抛出如下的异常：Caused by: java.lang.NoSuchMethodException: likly.java.reflect.Person.<init>()\n" +
                "  我们再次添加上空参的构造函数，并修改为如下： public Person() { System.out.println(\"Person\"); }\n" +
                "  然后再次执行测试程序，运行结果为：PersonPerson{name='likly', age=18}\n" +
                "  由此可认识，通过Class.newInstance()方式创建类对象的对象实例，本质是执行了类对象的默认的空参的构造函数，如果类对象含有非空的构造函数，并且没有显式的声明空参的构造函数，通过Class.newInstance()方式来创建类对象的实例时，会抛出java.lang.NoSuchMethodException异常。因此，开发者在设计通过反射创建类对象的对象实例时，一定要判断区分空参的构造方法。\n" +
                "  既然Class.newInstance()方法是通过调用类对象的空参的构造方法来创建类对象实例的，那是不是也可以调用非空的构造方法来创建类对象实例呢？当然是可以的，下面，我们先来说明如何获取类对象的构造方法。获取类对象的构造方法——Constructor\n" +
                "  Constructor是Java反射机制中的构造函数对象，获取该对象的方法有以下几种：\n" +
                "  Class.getConstructors():获取类对象的所有构造函数\n" +
                "Class.getConstructor(Class... paramTypes):获取指定的构造函数获取类对象所有的构造方法并遍历\n" +
                "  编写如下的测试代码：public class ConstructorInstance { public static void main(String[] args) { Class p = Person.class; for(Constructor constructor : p.getConstructors()){ System.out.println(constructor); } }}\n" +
                "  运行后结果为：public likly.java.reflect.Person()public likly.java.reflect.Person(java.lang.String,int)获取指定的构造方法\n" +
                "  通过Class.getConstructor(Class... paramTypes)即可获取类对象指定的构造方法，其中paramTypes为参数类型的Class可变参数，当不传paramTypes时，获取的构造方法即为默认的构造方法。\n" +
                "  获取默认的构造方法\n" +
                "  测试代码如下：public class ConstructorInstance { public static void main(String[] args) throws Exception { Class p = Person.class; Constructor constructor = p.getConstructor(); System.out.println(constructor); }}\n" +
                "  运行后结果为：public likly.java.reflect.Person()\n" +
                "  获取指定参数的构造方法\n" +
                "  这里以Person(String,int)为例\n" +
                "  测试代码如下：public class ConstructorInstance { public static void main(String[] args) throws Exception { Class p = Person.class; Constructor constructor = p.getConstructor(String.class,int.class); System.out.println(constructor); }}\n" +
                "  运行后结果为：public likly.java.reflect.Person(java.lang.String,int)\n" +
                "  从以上测试结果可知，可以通过Class.p.getConstructor(Class... paramTypes)来获取类对象特定的构造方法，如Person(String,int)。\n" +
                "  获取到构造方法有什么用呢？用处当然是来创建类的实例对象了。通过构造方法创建实例对象\n" +
                "  Constructor对象中有一个方法newInstance(Object ... initargs)，这里的initargs即为要传给构造方法的参数，如Person(String,int)，通过其对应的Constructor实例，调用newInstance方法并传入相应的参数，即可通过Person(String,int)来创建类对象的实例对象。\n" +
                "  测试代码如下：public class ConstructorInstance { public static void main(String[] args) throws Exception { Class p = Person.class; Constructor constructor = p.getConstructor(String.class,int.class); Person person = (Person) constructor.newInstance(\"Likly\",23); System.out.println(person); }}\n" +
                "  运行后结果为：Person{name='Likly', age=23}\n" +
                "  到这，创建对象实例的方法又掌握了一种，除了通过new来创建对象实例，还可以通过反射获取构造方法实例，然后再创建对象实例，不过，这里要注意的是，在使用Class.newInstance()时一定要注意类对象没有默认的空参的构造函数的情况。 ");
        data.setPubdate(new Date());
        data.setPubtime("2017-07-25");
        data.setMd5("1669f66b255dc67fcd9840f8147d7d65");
        dbService.saveData(data);

    }

    @Test
    public void checkTest(){
        NewsData data = new NewsData();
        data.setTitle("test");
        data.setUrl("testurl");

        dbService.checkData(data);
        System.out.println("ok.");
    }

}
