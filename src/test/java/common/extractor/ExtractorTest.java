package common.extractor;

import common.extractor.xpath.XpathExtractor;
import common.pojos.CommonData;
import common.pojos.DataHelper;
import common.pojos.CrawlTask;
import common.pojos.NewsData;
import common.system.AppContext;
import common.system.Systemconfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by guanxiaoda on 2017/7/19.
 */
public class ExtractorTest {


    @BeforeClass
    public static void beforeClass(){
        Systemconfig.crawlerType = 1;


        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }


    @Test
    public void createExtractorTest() throws IOException, SAXException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        CrawlTask task = new CrawlTask();
        task.setOrignUrl("http://guanxiaoda.cn");
        task.setMediaType(1);
        task.setSite("baidu");
        task.setCrawlerType("data");
        task.setEncode("utf-8");

        CommonData data = new NewsData();
        data.setTitle("关晓炟");
        data.setUrl("http://guanxiaoda.cn");
        data.setPubdate(new Date());

        task.setData(data);


        List listData = DataHelper.createDataList(1);

        XpathExtractor extractor = ExtractorHelper.createExtractor(task, "news_search", "news");
        System.out.println(extractor.toString());

        task.setContent(content);


        extractor.extract(task,listData);

        System.out.println(((NewsData)listData.get(0)).getTitle());
        System.out.println(((NewsData)listData.get(0)).getContent());
        System.out.println("ok.");
    }




    private static String content = "\n" +
            "<!DOCTYPE html>\n" +
            "<!--[if IE 6]><html class=\"ie lt-ie8\"><![endif]-->\n" +
            "<!--[if IE 7]><html class=\"ie lt-ie8\"><![endif]-->\n" +
            "<!--[if IE 8]><html class=\"ie ie8\"><![endif]-->\n" +
            "<!--[if IE 9]><html class=\"ie ie9\"><![endif]-->\n" +
            "<!--[if !IE]><!--> <html> <!--<![endif]-->\n" +
            "\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\">\n" +
            "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,user-scalable=no\">\n" +
            "\n" +
            "  <!-- Start of Baidu Transcode -->\n" +
            "  <meta http-equiv=\"Cache-Control\" content=\"no-siteapp\" />\n" +
            "  <meta http-equiv=\"Cache-Control\" content=\"no-transform\" />\n" +
            "  <meta name=\"applicable-device\" content=\"pc,mobile\">\n" +
            "  <meta name=\"MobileOptimized\" content=\"width\"/>\n" +
            "  <meta name=\"HandheldFriendly\" content=\"true\"/>\n" +
            "  <meta name=\"mobile-agent\" content=\"format=html5;url=http://www.jianshu.com/p/3acac743106c\">\n" +
            "  <!-- End of Baidu Transcode -->\n" +
            "\n" +
            "    <meta name=\"description\"  content=\"在Java语言中，除了通过new关键字来创建类对象的实例，还可以通过反射技术来创建类对象的实例。 通过反射来创建类对象的实例，首先我们得拿到类对象的Class，如何获取到Class，请参考以下内容： 简书：Java反射——获取Class对象 在拿到类对象的Class后，就可以通过Java的反射机制来创建类对象的实例对象了，主要分为以下几种方式： Class.newInstance() 调用类...\">\n" +
            "\n" +
            "  <meta name=\"360-site-verification\" content=\"604a14b53c6b871206001285921e81d8\" />\n" +
            "  <meta property=\"wb:webmaster\" content=\"294ec9de89e7fadb\" />\n" +
            "  <meta property=\"qc:admins\" content=\"104102651453316562112116375\" />\n" +
            "  <meta property=\"qc:admins\" content=\"11635613706305617\" />\n" +
            "  <meta property=\"qc:admins\" content=\"1163561616621163056375\" />\n" +
            "  <meta name=\"google-site-verification\" content=\"cV4-qkUJZR6gmFeajx_UyPe47GW9vY6cnCrYtCHYNh4\" />\n" +
            "  <meta name=\"google-site-verification\" content=\"HF7lfF8YEGs1qtCE-kPml8Z469e2RHhGajy6JPVy5XI\" />\n" +
            "  <meta http-equiv=\"mobile-agent\" content=\"format=html5; url=http://www.jianshu.com/p/3acac743106c\">\n" +
            "\n" +
            "  <!-- Apple -->\n" +
            "  <meta name=\"apple-mobile-web-app-title\" content=\"简书\">\n" +
            "\n" +
            "    <!--  Meta for Smart App Banner -->\n" +
            "  <meta name=\"apple-itunes-app\" content=\"app-id=888237539, app-argument=jianshu://notes/10746518\">\n" +
            "  <!-- End -->\n" +
            "\n" +
            "  <!--  Meta for Twitter Card -->\n" +
            "  <meta content=\"summary\" property=\"twitter:card\">\n" +
            "  <meta content=\"@jianshucom\" property=\"twitter:site\">\n" +
            "  <meta content=\"Java反射——创建对象实例\" property=\"twitter:title\">\n" +
            "  <meta content=\"在Java语言中，除了通过new关键字来创建类对象的实例，还可以通过反射技术来创建类对象的实例。 通过反射来创建类对象的实例，首先我们得拿到类对象的Class，如何获取到Class，请参考以下内容： 简书：Java反射——获取Class对象 在拿到类对象的Class后，就可以通过Java的反射机制来创建类对象的实例对象了，主要分为以下几种方式： Class.newInstance() 调用类对象的构造方法 测试类 首先，准备测试类如下： Class.newInstance() 测试代码如下： 运行结果为： 可见，通过Class.newInstance()方法是可以创建类对象的实例对象的，...\" property=\"twitter:description\">\n" +
            "  <meta content=\"http://www.jianshu.com/p/3acac743106c\" property=\"twitter:url\">\n" +
            "  <!-- End -->\n" +
            "\n" +
            "  <!--  Meta for OpenGraph -->\n" +
            "  <meta property=\"fb:app_id\" content=\"865829053512461\">\n" +
            "  <meta property=\"og:site_name\" content=\"简书\">\n" +
            "  <meta property=\"og:title\" content=\"Java反射——创建对象实例\">\n" +
            "  <meta property=\"og:type\" content=\"article\">\n" +
            "  <meta property=\"og:url\" content=\"http://www.jianshu.com/p/3acac743106c\">\n" +
            "  <meta property=\"og:description\" content=\"在Java语言中，除了通过new关键字来创建类对象的实例，还可以通过反射技术来创建类对象的实例。 通过反射来创建类对象的实例，首先我们得拿到类对象的Class，如何获取到Class，请参考以下内...\">\n" +
            "  <!-- End -->\n" +
            "\n" +
            "  <!--  Meta for Facebook Applinks -->\n" +
            "  <meta property=\"al:ios:url\" content=\"jianshu://notes/10746518\" />\n" +
            "  <meta property=\"al:ios:app_store_id\" content=\"888237539\" />\n" +
            "  <meta property=\"al:ios:app_name\" content=\"简书\" />\n" +
            "\n" +
            "  <meta property=\"al:android:url\" content=\"jianshu://notes/10746518\" />\n" +
            "  <meta property=\"al:android:package\" content=\"com.jianshu.haruki\" />\n" +
            "  <meta property=\"al:android:app_name\" content=\"简书\" />\n" +
            "  <!-- End -->\n" +
            "\n" +
            "\n" +
            "    <title>Java反射——创建对象实例 - 简书</title>\n" +
            "\n" +
            "  <meta name=\"csrf-param\" content=\"authenticity_token\" />\n" +
            "<meta name=\"csrf-token\" content=\"zn30bRT2tzWtXEMEyr6pmyzO7X7SO1Vqqixn9cJXbqxiAGc1cggjlqwA1YqOyw2KF8Pck/9hQxDV4/ndD6qW3A==\" />\n" +
            "\n" +
            "  <link rel=\"stylesheet\" media=\"all\" href=\"//cdn2.jianshu.io/assets/web-1f9f8400bf212abb741e.css\" />\n" +
            "  \n" +
            "  <link rel=\"stylesheet\" media=\"all\" href=\"//cdn2.jianshu.io/assets/web/pages/notes/show/entry-1f9f8400bf212abb741e.css\" />\n" +
            "\n" +
            "  <link href=\"//cdn2.jianshu.io/assets/favicons/favicon-783beb88ed621ceab614de960376ac0c.ico\" rel=\"icon\">\n" +
            "      <link rel=\"apple-touch-icon-precomposed\" href=\"//cdn2.jianshu.io/assets/apple-touch-icons/57-47624b2e2161e8eb144462c85db0a5ff.png\" sizes=\"57x57\" />\n" +
            "      <link rel=\"apple-touch-icon-precomposed\" href=\"//cdn2.jianshu.io/assets/apple-touch-icons/72-c00cde7cf98fc49e50cbb3ee1dcd5804.png\" sizes=\"72x72\" />\n" +
            "      <link rel=\"apple-touch-icon-precomposed\" href=\"//cdn2.jianshu.io/assets/apple-touch-icons/76-e8af0bdeaf1ba31e303b1fde8b5e66c4.png\" sizes=\"76x76\" />\n" +
            "      <link rel=\"apple-touch-icon-precomposed\" href=\"//cdn2.jianshu.io/assets/apple-touch-icons/114-f4c78569bbf1977e8382a5fd90c9237a.png\" sizes=\"114x114\" />\n" +
            "      <link rel=\"apple-touch-icon-precomposed\" href=\"//cdn2.jianshu.io/assets/apple-touch-icons/120-cf10c3711dba269522743729efe66bbc.png\" sizes=\"120x120\" />\n" +
            "      <link rel=\"apple-touch-icon-precomposed\" href=\"//cdn2.jianshu.io/assets/apple-touch-icons/152-7bd60457b5f3ecbf1343f0e6241be4f8.png\" sizes=\"152x152\" />\n" +
            "</head>\n" +
            "\n" +
            "  <body lang=\"zh-CN\" class=\"reader-black-font\">\n" +
            "    <!-- 全局顶部导航栏 -->\n" +
            "<nav class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\">\n" +
            "  <div class=\"width-limit\">\n" +
            "    <!-- 左上方 Logo -->\n" +
            "    <a class=\"logo\" href=\"/\"><img src=\"//cdn2.jianshu.io/assets/web/logo-58fd04f6f0de908401aa561cda6a0688.png\" alt=\"Logo\" /></a>\n" +
            "\n" +
            "    <!-- 右上角 -->\n" +
            "      <!-- 未登录显示登录/注册/写文章 -->\n" +
            "      <a class=\"btn write-btn\" target=\"_blank\" href=\"/writer#/\">\n" +
            "        <i class=\"iconfont ic-write\"></i>写文章\n" +
            "</a>      <a class=\"btn sign-up\" href=\"/sign_up\">注册</a>\n" +
            "      <a class=\"btn log-in\" href=\"/sign_in\">登录</a>\n" +
            "\n" +
            "    <!-- 如果用户登录，显示下拉菜单 -->\n" +
            "\n" +
            "    <div id=\"view-mode-ctrl\">\n" +
            "    </div>\n" +
            "    <div class=\"container\">\n" +
            "      <div class=\"navbar-header\">\n" +
            "        <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#menu\" aria-expanded=\"false\">\n" +
            "          <span class=\"icon-bar\"></span>\n" +
            "          <span class=\"icon-bar\"></span>\n" +
            "          <span class=\"icon-bar\"></span>\n" +
            "        </button>\n" +
            "      </div>\n" +
            "      <div class=\"collapse navbar-collapse\" id=\"menu\">\n" +
            "        <ul class=\"nav navbar-nav\">\n" +
            "            <li class=\"\">\n" +
            "              <a href=\"/\">\n" +
            "                <span class=\"menu-text\">首页</span><i class=\"iconfont ic-navigation-discover menu-icon\"></i>\n" +
            "</a>            </li>\n" +
            "            <li class=\"\">\n" +
            "              <a class=\"app-download-btn\" href=\"/apps\"><span class=\"menu-text\">下载App</span><i class=\"iconfont ic-navigation-download menu-icon\"></i></a>\n" +
            "            </li>\n" +
            "          <li class=\"search\">\n" +
            "            <form target=\"_blank\" action=\"/search\" accept-charset=\"UTF-8\" method=\"get\"><input name=\"utf8\" type=\"hidden\" value=\"&#x2713;\" />\n" +
            "              <input type=\"text\" name=\"q\" id=\"q\" value=\"\" placeholder=\"搜索\" class=\"search-input\" />\n" +
            "              <a class=\"search-btn\" href=\"javascript:void(null)\"><i class=\"iconfont ic-search\"></i></a>\n" +
            "              <!-- <div id=\"navbar-trending-search\"></div> -->\n" +
            "</form>          </li>\n" +
            "        </ul>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "  </div>\n" +
            "</nav>\n" +
            "\n" +
            "    \n" +
            "<div class=\"note\">\n" +
            "  <div class=\"post\">\n" +
            "    <div class=\"article\">\n" +
            "        <h1 class=\"title\">Java反射——创建对象实例</h1>\n" +
            "\n" +
            "        <!-- 作者区域 -->\n" +
            "        <div class=\"author\">\n" +
            "          <a class=\"avatar\" href=\"/u/c405c29b9b6e\">\n" +
            "            <img src=\"//cdn2.jianshu.io/assets/default_avatar/9-cceda3cf5072bcdd77e8ca4f21c40998.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/144/h/144\" alt=\"144\" />\n" +
            "</a>          <div class=\"info\">\n" +
            "            <span class=\"tag\">作者</span>\n" +
            "            <span class=\"name\"><a href=\"/u/c405c29b9b6e\">likly</a></span>\n" +
            "            <!-- 关注用户按钮 -->\n" +
            "            <div data-author-follow-button></div>\n" +
            "            <!-- 文章数据信息 -->\n" +
            "            <div class=\"meta\">\n" +
            "              <!-- 如果文章更新时间大于发布时间，那么使用 tooltip 显示更新时间 -->\n" +
            "                <span class=\"publish-time\">2017.03.29 19:21</span>\n" +
            "              <span class=\"wordage\">字数 1028</span>\n" +
            "            </div>\n" +
            "          </div>\n" +
            "          <!-- 如果是当前作者，加入编辑按钮 -->\n" +
            "        </div>\n" +
            "        <!-- -->\n" +
            "\n" +
            "        <!-- 文章内容 -->\n" +
            "        <div data-note-content class=\"show-content\">\n" +
            "          <p>在Java语言中，除了通过<code>new</code>关键字来创建类对象的实例，还可以通过反射技术来创建类对象的实例。</p>\n" +
            "<p>通过反射来创建类对象的实例，首先我们得拿到类对象的<code>Class</code>，如何获取到<code>Class</code>，请参考以下内容：</p>\n" +
            "<ul>\n" +
            "<li><a href=\"http://www.jianshu.com/p/efaa8567fce6\" target=\"_blank\">简书：Java反射——获取Class对象</a></li>\n" +
            "</ul>\n" +
            "<p>在拿到类对象的<code>Class</code>后，就可以通过Java的反射机制来创建类对象的实例对象了，主要分为以下几种方式：</p>\n" +
            "<ul>\n" +
            "<li>Class.newInstance()</li>\n" +
            "<li>调用类对象的构造方法</li>\n" +
            "</ul>\n" +
            "<h2>测试类</h2>\n" +
            "<p>首先，准备测试类如下：</p>\n" +
            "<pre><code class=\"java\">/**\n" +
            " * @author likly\n" +
            " * @version 1.0\n" +
            " */\n" +
            "public class Person {\n" +
            "    private String name;\n" +
            "    private int age;\n" +
            "\n" +
            "\n" +
            "    public Person() {\n" +
            "    }\n" +
            "\n" +
            "    public Person(String name, int age) {\n" +
            "        this.name = name;\n" +
            "        this.age = age;\n" +
            "    }\n" +
            "\n" +
            "    public String getName() {\n" +
            "        return name;\n" +
            "    }\n" +
            "\n" +
            "    public void setName(String name) {\n" +
            "        this.name = name;\n" +
            "    }\n" +
            "\n" +
            "    public int getAge() {\n" +
            "        return age;\n" +
            "    }\n" +
            "\n" +
            "    public void setAge(int age) {\n" +
            "        this.age = age;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"Person{\" +\n" +
            "                \"name='\" + name + '\\'' +\n" +
            "                \", age=\" + age +\n" +
            "                '}';\n" +
            "    }\n" +
            "}</code></pre>\n" +
            "<h2>Class.newInstance()</h2>\n" +
            "<p>测试代码如下：</p>\n" +
            "<pre><code class=\"java\">public class ClassNewInstance {\n" +
            "    public static void main(String[] args) throws IllegalAccessException, InstantiationException {\n" +
            "        Person person = Person.class.newInstance();\n" +
            "        person.setAge(18);\n" +
            "        person.setName(\"likly\");\n" +
            "        System.out.println(person);\n" +
            "    }\n" +
            "}</code></pre>\n" +
            "<p>运行结果为：</p>\n" +
            "<pre><code>Person{name='likly', age=18}</code></pre>\n" +
            "<p>可见，通过<code>Class.newInstance()</code>方法是可以创建类对象的实例对象的，但是一定要注意，如果把<code>Person</code>的空参的构造函数去掉，并创建非空的构造函数，再次运行上面的代码，将会抛出如下的异常：</p>\n" +
            "<pre><code>Caused by: java.lang.NoSuchMethodException: likly.java.reflect.Person.&lt;init&gt;()</code></pre>\n" +
            "<p>我们再次添加上空参的构造函数，并修改为如下：</p>\n" +
            "<pre><code class=\"java\">    public Person() {\n" +
            "        System.out.println(\"Person\");\n" +
            "    }</code></pre>\n" +
            "<p>然后再次执行测试程序，运行结果为：</p>\n" +
            "<pre><code>Person\n" +
            "Person{name='likly', age=18}</code></pre>\n" +
            "<p>由此可认识，通过<code>Class.newInstance()</code>方式创建类对象的对象实例，本质是执行了类对象的默认的空参的构造函数，如果类对象含有非空的构造函数，并且没有<strong>显式</strong>的声明空参的构造函数，通过<code>Class.newInstance()</code>方式来创建类对象的实例时，会抛出<code>java.lang.NoSuchMethodException</code>异常。因此，开发者在设计通过反射创建类对象的对象实例时，一定要判断区分空参的构造方法。</p>\n" +
            "<p>既然<code>Class.newInstance()</code>方法是通过调用类对象的空参的构造方法来创建类对象实例的，那是不是也可以调用非空的构造方法来创建类对象实例呢？当然是可以的，下面，我们先来说明如何获取类对象的构造方法。</p>\n" +
            "<h2>获取类对象的构造方法——Constructor</h2>\n" +
            "<p><code>Constructor</code>是Java反射机制中的构造函数对象，获取该对象的方法有以下几种：</p>\n" +
            "<ul>\n" +
            "<li>Class.getConstructors():获取类对象的所有构造函数</li>\n" +
            "<li>Class.getConstructor(Class... paramTypes):获取指定的构造函数</li>\n" +
            "</ul>\n" +
            "<h3>获取类对象所有的构造方法并遍历</h3>\n" +
            "<p>编写如下的测试代码：</p>\n" +
            "<pre><code class=\"java\">public class ConstructorInstance {\n" +
            "    public static void main(String[] args) {\n" +
            "        Class p = Person.class;\n" +
            "        for(Constructor constructor : p.getConstructors()){\n" +
            "            System.out.println(constructor);\n" +
            "        }\n" +
            "    }\n" +
            "}</code></pre>\n" +
            "<p>运行后结果为：</p>\n" +
            "<pre><code>public likly.java.reflect.Person()\n" +
            "public likly.java.reflect.Person(java.lang.String,int)</code></pre>\n" +
            "<h3>获取指定的构造方法</h3>\n" +
            "<p>通过<code>Class.getConstructor(Class... paramTypes)</code>即可获取类对象指定的构造方法，其中<code>paramTypes</code>为参数类型的<code>Class</code>可变参数，当不传<code>paramTypes</code>时，获取的构造方法即为默认的构造方法。</p>\n" +
            "<ul>\n" +
            "<li>获取默认的构造方法</li>\n" +
            "</ul>\n" +
            "<p>测试代码如下：</p>\n" +
            "<pre><code class=\"java\">public class ConstructorInstance {\n" +
            "    public static void main(String[] args) throws Exception {\n" +
            "        Class p = Person.class;\n" +
            "        Constructor constructor = p.getConstructor();\n" +
            "        System.out.println(constructor);\n" +
            "    }\n" +
            "}</code></pre>\n" +
            "<p>运行后结果为：</p>\n" +
            "<pre><code>public likly.java.reflect.Person()</code></pre>\n" +
            "<ul>\n" +
            "<li>获取指定参数的构造方法</li>\n" +
            "</ul>\n" +
            "<p>这里以<code>Person(String,int)</code>为例</p>\n" +
            "<p>测试代码如下：</p>\n" +
            "<pre><code class=\"java\">public class ConstructorInstance {\n" +
            "    public static void main(String[] args) throws Exception {\n" +
            "        Class p = Person.class;\n" +
            "\n" +
            "        Constructor constructor = p.getConstructor(String.class,int.class);\n" +
            "        System.out.println(constructor);\n" +
            "    }\n" +
            "}</code></pre>\n" +
            "<p>运行后结果为：</p>\n" +
            "<pre><code>public likly.java.reflect.Person(java.lang.String,int)</code></pre>\n" +
            "<p>从以上测试结果可知，可以通过<code>Class.p.getConstructor(Class... paramTypes)</code>来获取类对象特定的构造方法，如<code>Person(String,int)</code>。</p>\n" +
            "<p>获取到构造方法有什么用呢？用处当然是来创建类的实例对象了。</p>\n" +
            "<h2>通过构造方法创建实例对象</h2>\n" +
            "<p><code>Constructor</code>对象中有一个方法<code>newInstance(Object ... initargs)</code>，这里的<code>initargs</code>即为要传给构造方法的参数，如<code>Person(String,int)</code>，通过其对应的<code>Constructor</code>实例，调用<code>newInstance</code>方法并传入相应的参数，即可通过<code>Person(String,int)</code>来创建类对象的实例对象。</p>\n" +
            "<p>测试代码如下：</p>\n" +
            "<pre><code class=\"java\">public class ConstructorInstance {\n" +
            "    public static void main(String[] args) throws Exception {\n" +
            "        Class p = Person.class;\n" +
            "\n" +
            "        Constructor constructor = p.getConstructor(String.class,int.class);\n" +
            "        Person person = (Person) constructor.newInstance(\"Likly\",23);\n" +
            "        System.out.println(person);    }\n" +
            "}</code></pre>\n" +
            "<p>运行后结果为：</p>\n" +
            "<pre><code>Person{name='Likly', age=23}</code></pre>\n" +
            "<p>到这，创建对象实例的方法又掌握了一种，除了通过<code>new</code>来创建对象实例，还可以通过反射获取构造方法实例，然后再创建对象实例，不过，这里要注意的是，在使用<code>Class.newInstance()</code>时一定要注意类对象没有默认的空参的构造函数的情况。</p>\n" +
            "\n" +
            "        </div>\n" +
            "        <!--  -->\n" +
            "\n" +
            "        <div class=\"show-foot\">\n" +
            "          <a class=\"notebook\" href=\"/nb/11292105\">\n" +
            "            <i class=\"iconfont ic-search-notebook\"></i> <span>Java反射</span>\n" +
            "</a>          <div class=\"copyright\" data-toggle=\"tooltip\" data-html=\"true\" data-original-title=\"转载请联系作者获得授权，并标注“简书作者”。\">\n" +
            "            © 著作权归作者所有\n" +
            "          </div>\n" +
            "          <div class=\"modal-wrap\" data-report-note>\n" +
            "            <a id=\"report-modal\">举报文章</a>\n" +
            "          </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <!-- 文章底部作者信息 -->\n" +
            "      <div class=\"follow-detail\">\n" +
            "        <div class=\"info\">\n" +
            "          <a class=\"avatar\" href=\"/u/c405c29b9b6e\">\n" +
            "            <img src=\"//cdn2.jianshu.io/assets/default_avatar/9-cceda3cf5072bcdd77e8ca4f21c40998.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/144/h/144\" alt=\"144\" />\n" +
            "</a>          <div data-author-follow-button></div>\n" +
            "          <a class=\"title\" href=\"/u/c405c29b9b6e\">likly</a>\n" +
            "        </div>\n" +
            "      </div>\n" +
            "\n" +
            "      <div class=\"support-author\"></div>\n" +
            "\n" +
            "    <div class=\"meta-bottom\">\n" +
            "      <div class=\"btn like-group\"></div>\n" +
            "      <div class=\"share-group\">\n" +
            "        <a class=\"share-circle\" data-action=\"weixin-share\" data-toggle=\"tooltip\" data-original-title=\"分享到微信\">\n" +
            "          <i class=\"iconfont ic-wechat\"></i>\n" +
            "        </a>\n" +
            "        <a class=\"share-circle\" data-toggle=\"tooltip\" href=\"javascript:void((function(s,d,e,r,l,p,t,z,c){var%20f=&#39;http://v.t.sina.com.cn/share/share.php?appkey=1881139527&#39;,u=z||d.location,p=[&#39;&amp;url=&#39;,e(u),&#39;&amp;title=&#39;,e(t||d.title),&#39;&amp;source=&#39;,e(r),&#39;&amp;sourceUrl=&#39;,e(l),&#39;&amp;content=&#39;,c||&#39;gb2312&#39;,&#39;&amp;pic=&#39;,e(p||&#39;&#39;)].join(&#39;&#39;);function%20a(){if(!window.open([f,p].join(&#39;&#39;),&#39;mb&#39;,[&#39;toolbar=0,status=0,resizable=1,width=440,height=430,left=&#39;,(s.width-440)/2,&#39;,top=&#39;,(s.height-430)/2].join(&#39;&#39;)))u.href=[f,p].join(&#39;&#39;);};if(/Firefox/.test(navigator.userAgent))setTimeout(a,0);else%20a();})(screen,document,encodeURIComponent,&#39;&#39;,&#39;&#39;,&#39;http://cwb.assets.jianshu.io/notes/images/10746518/weibo/image_160d78054594.jpg&#39;, &#39;推荐 likly 的文章《Java反射——创建对象实例》（ 分享自 @简书 ）&#39;,&#39;http://www.jianshu.com/p/3acac743106c?utm_campaign=maleskine&amp;utm_content=note&amp;utm_medium=reader_share&amp;utm_source=weibo&#39;,&#39;页面编码gb2312|utf-8默认gb2312&#39;));\" data-original-title=\"分享到微博\">\n" +
            "          <i class=\"iconfont ic-weibo\"></i>\n" +
            "        </a>\n" +
            "          <a class=\"share-circle\" data-toggle=\"tooltip\" href=\"http://cwb.assets.jianshu.io/notes/images/10746518/weibo/image_160d78054594.jpg\" target=\"_blank\" data-original-title=\"下载长微博图片\">\n" +
            "            <i class=\"iconfont ic-picture\"></i>\n" +
            "          </a>\n" +
            "        <a class=\"share-circle more-share\" tabindex=\"0\" data-toggle=\"popover\" data-placement=\"top\" data-html=\"true\" data-trigger=\"focus\" href=\"javascript:void(0);\" data-content='\n" +
            "          <ul class=\"share-list\">\n" +
            "            <li><a href=\"javascript:void(function(){var d=document,e=encodeURIComponent,r=&#39;http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url=&#39;+e(&#39;http://www.jianshu.com/p/3acac743106c?utm_campaign=maleskine&amp;utm_content=note&amp;utm_medium=reader_share&amp;utm_source=qzone&#39;)+&#39;&amp;title=&#39;+e(&#39;推荐 likly 的文章《Java反射——创建对象实例》&#39;),x=function(){if(!window.open(r,&#39;qzone&#39;,&#39;toolbar=0,resizable=1,scrollbars=yes,status=1,width=600,height=600&#39;))location.href=r};if(/Firefox/.test(navigator.userAgent)){setTimeout(x,0)}else{x()}})();\"><i class=\"social-icon-sprite social-icon-zone\"></i><span>分享到QQ空间</span></a></li>\n" +
            "            <li><a href=\"javascript:void(function(){var d=document,e=encodeURIComponent,r=&#39;https://twitter.com/share?url=&#39;+e(&#39;http://www.jianshu.com/p/3acac743106c?utm_campaign=maleskine&amp;utm_content=note&amp;utm_medium=reader_share&amp;utm_source=twitter&#39;)+&#39;&amp;text=&#39;+e(&#39;推荐 likly 的文章《Java反射——创建对象实例》（ 分享自 @jianshucom ）&#39;)+&#39;&amp;related=&#39;+e(&#39;jianshucom&#39;),x=function(){if(!window.open(r,&#39;twitter&#39;,&#39;toolbar=0,resizable=1,scrollbars=yes,status=1,width=600,height=600&#39;))location.href=r};if(/Firefox/.test(navigator.userAgent)){setTimeout(x,0)}else{x()}})();\"><i class=\"social-icon-sprite social-icon-twitter\"></i><span>分享到Twitter</span></a></li>\n" +
            "            <li><a href=\"javascript:void(function(){var d=document,e=encodeURIComponent,r=&#39;https://www.facebook.com/dialog/share?app_id=483126645039390&amp;display=popup&amp;href=http://www.jianshu.com/p/3acac743106c?utm_campaign=maleskine&amp;utm_content=note&amp;utm_medium=reader_share&amp;utm_source=facebook&#39;,x=function(){if(!window.open(r,&#39;facebook&#39;,&#39;toolbar=0,resizable=1,scrollbars=yes,status=1,width=450,height=330&#39;))location.href=r};if(/Firefox/.test(navigator.userAgent)){setTimeout(x,0)}else{x()}})();\"><i class=\"social-icon-sprite social-icon-facebook\"></i><span>分享到Facebook</span></a></li>\n" +
            "            <li><a href=\"javascript:void(function(){var d=document,e=encodeURIComponent,r=&#39;https://plus.google.com/share?url=&#39;+e(&#39;http://www.jianshu.com/p/3acac743106c?utm_campaign=maleskine&amp;utm_content=note&amp;utm_medium=reader_share&amp;utm_source=google_plus&#39;),x=function(){if(!window.open(r,&#39;google_plus&#39;,&#39;toolbar=0,resizable=1,scrollbars=yes,status=1,width=450,height=330&#39;))location.href=r};if(/Firefox/.test(navigator.userAgent)){setTimeout(x,0)}else{x()}})();\"><i class=\"social-icon-sprite social-icon-google\"></i><span>分享到Google+</span></a></li>\n" +
            "            <li><a href=\"javascript:void(function(){var d=document,e=encodeURIComponent,s1=window.getSelection,s2=d.getSelection,s3=d.selection,s=s1?s1():s2?s2():s3?s3.createRange().text:&#39;&#39;,r=&#39;http://www.douban.com/recommend/?url=&#39;+e(&#39;http://www.jianshu.com/p/3acac743106c?utm_campaign=maleskine&amp;utm_content=note&amp;utm_medium=reader_share&amp;utm_source=douban&#39;)+&#39;&amp;title=&#39;+e(&#39;Java反射——创建对象实例&#39;)+&#39;&amp;sel=&#39;+e(s)+&#39;&amp;v=1&#39;,x=function(){if(!window.open(r,&#39;douban&#39;,&#39;toolbar=0,resizable=1,scrollbars=yes,status=1,width=450,height=330&#39;))location.href=r+&#39;&amp;r=1&#39;};if(/Firefox/.test(navigator.userAgent)){setTimeout(x,0)}else{x()}})()\"><i class=\"social-icon-sprite social-icon-douban\"></i><span>分享到豆瓣</span></a></li>\n" +
            "          </ul>\n" +
            "        '>更多分享</a>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div id=\"vue_comment\"></div>\n" +
            "  </div>\n" +
            "\n" +
            "  <div class=\"vue-side-tool\"></div>\n" +
            "</div>\n" +
            "<div class=\"note-bottom\">\n" +
            "  <div class=\"js-included-collections\"></div>\n" +
            "  <div data-vcomp=\"recommended-notes\" data-lazy=\"1.5\" data-note-id=\"10746518\"></div>\n" +
            "</div>\n" +
            "\n" +
            "    <script type=\"application/json\" data-name=\"page-data\">{\"user_signed_in\":false,\"locale\":\"zh-CN\",\"os\":\"mac\",\"read_mode\":\"day\",\"read_font\":\"font2\",\"note_show\":{\"is_author\":false,\"is_following_author\":false,\"is_liked_note\":false,\"uuid\":\"022da646-38c8-4c99-a152-07164210ce14\"},\"note\":{\"id\":10746518,\"slug\":\"3acac743106c\",\"user_id\":5415433,\"notebook_id\":11292105,\"commentable\":true,\"likes_count\":0,\"views_count\":137,\"public_wordage\":1028,\"comments_count\":0,\"total_rewards_count\":0,\"is_author\":false,\"author\":{\"nickname\":\"likly\",\"total_wordage\":1939,\"followers_count\":0,\"total_likes_count\":0}}}</script>\n" +
            "    \n" +
            "    <script src=\"//cdn2.jianshu.io/assets/babel-polyfill-879c5f4bc085ce8a6153.js\"></script>\n" +
            "    <script src=\"//cdn2.jianshu.io/assets/web-base-1f9f8400bf212abb741e.js\"></script>\n" +
            "<script src=\"//cdn2.jianshu.io/assets/web-908323f2007a79c38b82.js\"></script>\n" +
            "    \n" +
            "    <script src=\"//cdn2.jianshu.io/assets/web/pages/notes/show/entry-a0e28f4f66370f25efec.js\"></script>\n" +
            "    <script>\n" +
            "  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n" +
            "  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n" +
            "  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n" +
            "  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n" +
            "\n" +
            "  ga('create', 'UA-35169517-1', 'auto');\n" +
            "  ga('send', 'pageview');\n" +
            "</script>\n" +
            "\n" +
            "<script>\n" +
            "  var _hmt = _hmt || [];\n" +
            "  (function() {\n" +
            "    var hm = document.createElement(\"script\");\n" +
            "    hm.src = \"//hm.baidu.com/hm.js?0c0e9d9b1e7d617b3e6842e85b9fb068\";\n" +
            "    var s = document.getElementsByTagName(\"script\")[0];\n" +
            "    s.parentNode.insertBefore(hm, s);\n" +
            "  })();\n" +
            "</script>\n" +
            "\n" +
            "<script>\n" +
            "  (function(){\n" +
            "      var bp = document.createElement('script');\n" +
            "      var curProtocol = window.location.protocol.split(':')[0];\n" +
            "      if (curProtocol === 'https') {\n" +
            "          bp.src = 'https://zz.bdstatic.com/linksubmit/push.js';\n" +
            "      }\n" +
            "      else {\n" +
            "          bp.src = 'http://push.zhanzhang.baidu.com/push.js';\n" +
            "      }\n" +
            "      var s = document.getElementsByTagName(\"script\")[0];\n" +
            "      s.parentNode.insertBefore(bp, s);\n" +
            "  })();\n" +
            "</script>\n" +
            "\n" +
            "  </body>\n" +
            "</html>\n";
}
