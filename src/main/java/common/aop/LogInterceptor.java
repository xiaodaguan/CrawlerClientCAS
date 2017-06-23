package common.aop;

import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by guanxiaoda on 2017/6/23.
 */

@Aspect
@Component
public class LogInterceptor {
    private static Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);



    @Pointcut("execution(* common.other.*.sayHi())")
    private void whatever(){}

    @Before("whatever()")
    public void before(){
        LOGGER.info("aaaaaa this is before sayHi()");
    }

    @After("whatever()")
    public void after(){
        LOGGER.info("bbbbbb this is after sayHiiiiii()!");
    }

    @AfterReturning("whatever()")
    public void afterReturnning(){
        LOGGER.info("thiiiiiii is after returning");
    }


    @AfterThrowing("whatever()")
    public void afterThrowing(){
        LOGGER.info("this is after throwing......");

    }




}
