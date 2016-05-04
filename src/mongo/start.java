package mongo;

/**
 * Created by guanxiaoda on 16/4/29.
 */
public class start {



    public static void main(String[] args) {
        //保存
        transferData m2o = new transferData();
        Thread tMove = new Thread(m2o,"transfer");
        tMove.start();

        //更新
        updateCollect update = new updateCollect();
        Thread tUpdate = new Thread(update,"update");
        tUpdate.start();
    }

}
