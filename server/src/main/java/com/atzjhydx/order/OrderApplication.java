package com.atzjhydx.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.atzjhydx.product.client") //扫描FeignClient类所在包，由于不在本项目中
@ComponentScan(basePackages = "com.atzjhydx")
@SpringBootApplication
@EnableDiscoveryClient //注册到注册中心的注解
@EnableCircuitBreaker //使用熔断器注解
@EnableHystrixDashboard //熔断器可视化面板服务注解
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
