//package com.atzjhydx.order.controller;
//
//import com.atzjhydx.order.config.GirlConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 测试是否获取到统一配置中心的配置
// * @Auther LeeMZ
// * @Date 2021/1/30
// **/
//
//@RestController
//@RequestMapping("/env")
//@RefreshScope //重新刷新类使类内属性重新获取
//public class EnvController {
//
//    @Value("${env}")
//    private String env;
//
//    @Autowired
//    private GirlConfig girlConfig;
//
//    @GetMapping("/print")
//    public String print(){
//        return env;
//    }
//
//    @GetMapping("/girl/print")
//    public String getGirlPrint(){
//        return "name:" + girlConfig.getName() + "age:" + girlConfig.getAge();
//    }
//}
