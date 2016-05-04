package mongo;

/**
 * Created by guanxiaoda on 16/4/29.
 */
public class start {



    public static void main(String[] args) {
        //保存
        mongo2Ora m2o = new mongo2Ora();
        Thread tMove = new Thread(m2o,"move");
        tMove.start();

        //更新
        updateCollect update = new updateCollect();
        Thread tUpdate = new Thread(update,"update");
        tUpdate.start();
    }

}
