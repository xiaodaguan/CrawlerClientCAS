package common.system;

import common.util.TimeUtil;

public class TaskMonitor implements Runnable {

	@Override
	public void run() {
		while (true) {
			System.out.println("{{{");
			for (String site : Job.getExecutorServiceMap().keySet()) {
				System.out.println(site+" : "+Job.getExecutorServiceMap().get(site));
			}
			System.out.println("}}}");
			TimeUtil.rest(15 * 60);
		}
	}

}
