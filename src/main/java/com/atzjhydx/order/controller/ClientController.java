package com.atzjhydx.order.controller;

import com.atzjhydx.order.VO.ResultVO;
import com.atzjhydx.order.client.ProductClient;
import com.atzjhydx.order.dataobject.ProductInfo;
import com.atzjhydx.order.dto.CartDTO;
import com.netflix.client.IResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@RestController
@Slf4j
public class ClientController {

//    @Autowired
//    private LoadBalancerClient loadBalancerClient;

//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private ProductClient productClient;


    @GetMapping("/getProductMsg")
    public String getProductMsg(){

        // 1.第一种方式,直接使用restTemplate url写死
//        RestTemplate restTemplate = new RestTemplate();
//        //参数1为服务端请求接口，参数2返回值类型
//        String response = restTemplate.getForObject("http://localhost:8080/msg", String.class);
//        log.info("response={}",response);
//        return response;

        // 2.第二种方式,利用loadBalancerClient通过应用名获取url，然后再使用restTemplate
//        RestTemplate restTemplate = new RestTemplate();
//        // 通过服务名（application name）
//        ServiceInstance serviceInstance = loadBalancerClient.choose("PRODUCT");
//        String url = String.format("http://%s:%s",serviceInstance.getHost(),serviceInstance.getPort()) + "/msg";
//        String response = restTemplate.getForObject(url, String.class);

//        log.info("response={}", response);
//        return response;

        // 3.第三种方式，使用注解完成(利用@LoadBalanced，可在restTemplate里使用应用名字)
//        String response = restTemplate.getForObject("http://PRODUCT/msg", String.class);

        // 4.使用Feign通信
        String result = productClient.productMsg();

        return result;
    }

    @GetMapping("/getProductList")
    public List<ProductInfo> getProductList(){
        return productClient.getProductInfo(Arrays.asList("164103465734242707"));
    }

    @GetMapping("/productDecreaseStock")
    public String productDecreaseStock(){
        productClient.decreaseStock(Arrays.asList(new CartDTO("164103465734242707",3)));
        return "ok";
    }
}
