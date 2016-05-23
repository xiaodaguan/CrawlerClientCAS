package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class testThread {


    public static void main(String[] args) throws Exception {

//		testThread myThread = new testThread();
//		Thread threads[] = new Thread[100];
//		for (int i = 0; i < threads.length; i++) {
//			threads[i] = new Thread(myThread);
//		}
//		for(int i=0;i<threads.length;i++){
//			threads[i].start();
//		}
//
//		for(int i=0;i<threads.length;i++){
//			threads[i].join();
//		}
//
//		System.out.println("n: "+myThread.n);


        ExecutorService es = Executors.newFixedThreadPool(3);
        List<Future> fList = new ArrayList<Future>();


        for (int i = 0; i < 3; i++) {
            Future f = es.submit(new Runnable() {
                @Override
                public void run() {
                    String str = "";
                    for (int i = 0; i < 9999; i++) {
                        str += i + "";
                    }
//                    System.out.println(str);
                }
            });
            fList.add(f);
        }

        while (true) {
            for (int i = 0; i < 3; i++) {
                Future f = fList.get(i);
                if (f.isDone()) {
                    System.out.println("task finished.");
                } else {
                    System.out.println("task not finished");
                }

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