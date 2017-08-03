package common.downloader;




import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 耿 on 2016/8/12.
 */
public class RetryInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryInterceptor.class);

    public int maxRetry;//最大重试次数
    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        LOGGER.info("intercept(Chain chain)");
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            LOGGER.info("retryNum={}",retryNum);
            response = chain.proceed(request);
        }
        return response;
    }


}