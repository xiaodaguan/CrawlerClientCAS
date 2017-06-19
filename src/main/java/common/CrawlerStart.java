package common;

import java.text.SimpleDateFormat;
import java.util.Date;

import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerStart {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerStart.class);


    public static void main(String[] args) throws Exception {


        StringBuilder stringBuilder = parseArgs(args);
        if (stringBuilder == null){
            LOGGER.error("no args found.");
            return;
        }


        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        AppContext.initAppCtx(path);//初始化

        LOGGER.info("\n\n\n");
        LOGGER.info("[crawler start] current cmd: " + stringBuilder.toString());
        LOGGER.info("[crawler start] will start after 3 sec...");
        LOGGER.info("\n\n\n");


        LOGGER.info("table:\t" + Systemconfig.table);
        Thread.sleep(3 * 1000);
        Job.simpleRun();

    }

    public static StringBuilder parseArgs(String[] args) {
        if (args.length == 0) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String crawlerName = null;

        for (String arg : args) {

            stringBuilder.append(arg).append(" ");

            if (arg.toLowerCase().contains("type=")) { //type
                String value = arg.split("=")[1];//类型   

                try {
                    Systemconfig.crawlerType = Integer.parseInt(value);
                    Job.setcType(value);

                } catch (NumberFormatException nfe) {
                    LOGGER.error("type 错误.");
                    nfe.printStackTrace();
                }
            } else if (arg.toLowerCase().contains("name=")) {//name
                crawlerName = arg.split("=")[1];
            } else if (arg.toLowerCase().contains("project=")) {//project
                Job.setProject(arg.split("=")[1]);
            } else if (arg.toLowerCase().contains("crawlercount=")) {//需要分布式部署多少个爬虫
                Systemconfig.crawlerCount = Integer.parseInt(arg.split("=")[1]);
            } else if (arg.toLowerCase().contains("clientindex=")) {//当前是几号爬虫
                Systemconfig.crawlerNum = Integer.parseInt(arg.split("=")[1]);
            } else if (arg.toLowerCase().contains("note=")) {//note

            }else if(arg.toLowerCase().equals("-f")||arg.toLowerCase().equals("--force-init-bf")){
                Systemconfig.force_init_bf = 1;
            }
        }

        LOGGER.info(stringBuilder.toString());


        Job.setCrawlerNameOrCMD(stringBuilder.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String cid = "C" + Job.getProject().toUpperCase() + sdf.format(new Date()) + "000" + Job.getcType();
        Job.setCid(cid);

        if (Systemconfig.crawlerType == 0) {
            LOGGER.error("类别参数没有定义('type=n')");
            return null;
        }
        if (crawlerName == null) {
            LOGGER.error("名称参数没有定义('name=n')");
            return null;
        }
        if (Job.getCid().equals("")) {
            System.err.print("[warning]: cid not defined!");
        }
        if (Job.getProject().equals("")) {
            System.err.print("[warning]: project not defined!");
        }
        return stringBuilder;
    }
}




