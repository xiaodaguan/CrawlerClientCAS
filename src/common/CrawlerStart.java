package common;

import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;

import java.util.ArrayList;

public class CrawlerStart {

    public static void main(String[] args) throws Exception {


        // common.util.TimeUtil.rest(8 * 60 * 60);

        // TaskMonitor tm=new TaskMonitor();
        // Thread tmonitor=new Thread(tm);
        // tmonitor.start();

        for (String arg : args) {
            if (arg.toLowerCase().contains("type=")) {
                String value = arg.split("=")[1];
                try {
                    Systemconfig.crawlerType = Integer.parseInt(value);
                } catch (NumberFormatException nfe) {
                    System.err.println("参数错误.");
                    nfe.printStackTrace();
                }
            }
        }

        if(Systemconfig.crawlerType == 0){
            System.out.println("类别参数没有定义('type=n')");
            return;
        }

        AppContext.initAppCtx("");



        int life = Systemconfig.lifeCycle;

        if (Systemconfig.getDistribute()) Job.statusRun();
        else Job.simpleRun();

        // TimeUtil.rest(60 * life);
        // System.out.println(60 * life+"ok.");
        // System.exit(0);
    }

}
