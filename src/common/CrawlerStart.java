package common;

import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;
import crawlerlog.log.CLog;
import crawlerlog.log.CLogFactory;

public class CrawlerStart {


    private static CLog cLogger = CLogFactory.getLogger("t000000");

    public static void main(String[] args) throws Exception {


        // common.util.TimeUtil.rest(8 * 60 * 60);

        // TaskMonitor tm=new TaskMonitor();
        // Thread tmonitor=new Thread(tm);
        // tmonitor.start();

        StringBuilder stringBuilder = new StringBuilder();
        String crawlerName = null;
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
            if (arg.toLowerCase().contains("type=")) {
                String value = arg.split("=")[1];
                try {
                    Systemconfig.crawlerType = Integer.parseInt(value);
                } catch (NumberFormatException nfe) {
                    System.err.println("type 错误.");
                    nfe.printStackTrace();
                }
            } else if (arg.toLowerCase().contains("name=")) {
                crawlerName = arg.split("=")[1];

            }
        }


        if (Systemconfig.crawlerType == 0) {
            System.err.println("类别参数没有定义('type=n')");
            return;
        }

        if (crawlerName == null) {
            System.err.println("名称参数没有定义('name=n')");
            return;
        }

        cLogger.start(stringBuilder.toString(), crawlerName);

        AppContext.initAppCtx("");//初始化


        Job.simpleRun();//任务运行

    }

}
