package common;

import common.download.GenericCommonDownload;
import common.pojos.HtmlInfo;
import common.system.AppContext;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guanxiaoda on 2017/7/4.
 */
public class Crawler {
    public static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    static{
        // 读取配置
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);
    }

    public Crawler(String crawlerName, int mediaType) {
        Systemconfig.crawlerType = mediaType;

    }


    public void start() {


        // 是否初始化url队列
        //
        while (true) {
            //  getTask()
            HtmlInfo task = Systemconfig.scheduler.getTask();
            if(task.getOrignUrl()==null){
                continue;
            }
            //  download()
            if (task.getCrawlerType().equalsIgnoreCase("META")) {

            } else if (task.getCrawlerType().equalsIgnoreCase("DATA")) {

            }
            //  parse()
            //  save()/submitTasks()
            //  handleException()
        }

    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler("TestNewsSearch", 1);
        crawler.start();
    }
}
