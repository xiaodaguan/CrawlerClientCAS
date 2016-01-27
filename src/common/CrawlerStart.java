package common;

import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;

public class CrawlerStart {

	public static void main(String[] args) throws Exception {

		// common.util.TimeUtil.rest(8 * 60 * 60);

		// TaskMonitor tm=new TaskMonitor();
        // Thread tmonitor=new Thread(tm);
        // tmonitor.start();
        AppContext.initAppCtx("");
        int life = Systemconfig.lifeCycle;

        if (Systemconfig.getDistribute())
            Job.statusRun();
        else
            Job.simpleRun();

        // TimeUtil.rest(60 * life);
        // System.out.println(60 * life+"ok.");
        // System.exit(0);
	}

}
