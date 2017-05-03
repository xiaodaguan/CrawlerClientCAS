package test;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class testThread {

    private static Logger logger = Logger.getLogger(testThread.class.getName());

    public static void main(String[] args) throws Exception {



        ExecutorService es = Executors.newFixedThreadPool(3);// ExecutorService 线程池
        List<Future> fList = new ArrayList<Future>();//TaskList


        for (int i = 0; i < 3; i++) {//启动线程,并将任务注册到TaskList
            Future f = es.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 333; i++) {//等待10秒
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
//                    System.out.println(str);
                }
            });
            fList.add(f);
        }

        while (true) 
        {
            boolean allDone = true;
            for (int i = 0; i < 3; i++) {
                Future f = fList.get(i);
                if (f.isDone()) {
                    logger.info("task finished.");
                } else {
                    logger.info("task not finished");
                    allDone = false;
                    break;
                }

            }

            if(allDone) {
                logger.info("all done!");
//                es.shutdown();
                break;
            }
            Thread.currentThread().sleep(1000);
        }
    }

    private String getTime() {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeInMillis(System.currentTimeMillis());
        return rightNow.get(Calendar.HOUR) + ":" + rightNow.get(Calendar.MINUTE) + ":" + rightNow.get(Calendar.SECOND);
    }
}