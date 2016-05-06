package mongo;

/**
 * Created by guanxiaoda on 16/4/29.
 * 启动进程
 */
public class start {



    public static void main(String[] args) {
        //文章搜索
//        transferData m2o = new transferData();
//        Thread tMove = new Thread(m2o,"transfer");
//        tMove.start();

        //公众号
        updateCollect update = new updateCollect();
        Thread tUpdate = new Thread(update,"update");
        tUpdate.start();
    }

}
