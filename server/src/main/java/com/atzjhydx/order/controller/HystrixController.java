package com.atzjhydx.order.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * @Auther LeeMZ
 * @Date 2021/2/2
 **/
@RestController
@DefaultProperties(defaultFallback = "defaultFallBack")
public class HystrixController {

//    //超时配置,超时时间超过3秒钟才触发服务降级（依赖隔离功能，会将依赖线程加入到新创建的线程池中，与当前请求线程隔离）
//    @HystrixCommand(commandProperties = {
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
//    })

    //服务熔断（写入到配置项中）
    //标志HystrixCommand后，application启动后会将所有标注该注解的方法生成一个代理对象后放入到Hystrix自己的线程池中
    //当调用方法发生失败时，代理方法中的try catch处理异常时通过反射的方式执行fallback的方法
    @HystrixCommand(commandProperties = {
        @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),  				//设置开启熔断
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),	//请求数达到后才计算
        //休眠时间窗，当服务降级后到达休眠时间窗口设定时间后，服务进入半开启状态，会将一部分的请求到达主逻辑，查看是否请求成功并计算成功率，到达一定正确率后服务恢复
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),	//熔断器打开的错误率条件
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1000")
    })
    //服务降级也可以用在本服务出现异常时使用
    @GetMapping("/getProductInfoList")
    public String getProductInfoList(@RequestParam("num")Integer number){

        if (number % 2 == 0){
            return "success";
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject("http://localhost:8082/product/listForOrder", Arrays.asList("157875227953464068"),String.class);

    }

    //服务降级后调用该方法
    private String fallback(){
        return "对不起，请稍后再试试!!!";
    }

    //默认提示
    private String defaultFallBack(){
        return "默认提示，太拥挤了";
    }
}
