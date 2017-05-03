package test;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorTest2 {
    public static void main(String[] args) throws Exception {

        Runnable hello = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println(i + " hello");
                try {
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        };
        Runnable bye =  () ->{
            for (int i = 0; i < 5; i++) {
                System.out.println(i + " bye"); 
                
                try {
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        };


        ExecutorService  executor = Executors.newFixedThreadPool(1);
       
        Future<?> f1 =  executor.submit(hello);
        //Future<?> f2 =  executor.submit(bye);

        Thread.sleep(1000);


        System.out.println(f1.cancel(false)+"  f1.cancel(true)");
        System.out.println(f1.cancel(true)+"  f1.cancel(true)");
        
        Future<?> f3 =  executor.submit(bye);
        if(f1.isDone()){
        	System.out.println("f2 close");
        }
        
        System.out.println(executor.isShutdown());
        
        System.out.println(executor.isShutdown());
        return ;
     
    }
}