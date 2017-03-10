package common;

import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;
import crawlerlog.log.CLog;
import crawlerlog.log.CLogFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CrawlerStart {


    public static void main(String[] args) throws Exception {
    	
        // common.util.TimeUtil.rest(8 * 60 * 60);
        // TaskMonitor tm=new TaskMonitor();
        // Thread tmonitor=new Thread(tm);
        // tmonitor.start(); 

        if (args.length == 0) {
            return;
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
                    System.err.println("type 错误.");
                    nfe.printStackTrace();
                }
            } else if (arg.toLowerCase().contains("name=")) {//name
                crawlerName = arg.split("=")[1];
            } else if (arg.toLowerCase().contains("project=")) {//project
                Job.setProject(arg.split("=")[1]);
            }else if (arg.toLowerCase().contains("crawlercount=")) {//需要分布式部署多少个爬虫
            	Systemconfig.crawlerCount = Integer.parseInt(arg.split("=")[1]);
            }else if (arg.toLowerCase().contains("clientindex=")) {//当前是几号爬虫
            	Systemconfig.crawlerNum = Integer.parseInt(arg.split("=")[1]);
            } 
            else if (arg.toLowerCase().contains("note=")) {//note

            }
        }
        
        Systemconfig.sysLog.log(stringBuilder.toString());

       
        Job.setCrawlerNameOrCMD(stringBuilder.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String cid = "C" + Job.getProject().toUpperCase() + sdf.format(new Date()) + "000" + Job.getcType();
        Job.setCid(cid);

        if (Systemconfig.crawlerType == 0) {
            System.err.println("类别参数没有定义('type=n')");
            return;
        }
        if (crawlerName == null) {
            System.err.println("名称参数没有定义('name=n')");
            return;
        }
        if (Job.getCid().equals("")) {
            System.err.print("[warning]: cid not defined!");
        }
        if (Job.getProject().equals("")) {
            System.err.print("[warning]: project not defined!");
        }
       
        AppContext.initAppCtx("");//初始化
        Systemconfig.sysLog.log("\n\n\n");
        Systemconfig.sysLog.log("[crawler start] current cmd: " + stringBuilder.toString());
        Systemconfig.sysLog.log("[crawler start] will start after 3 sec...");
        Systemconfig.sysLog.log("\n\n\n");
        Thread.sleep(3 * 1000);
        Job.simpleRun();
    }
}




