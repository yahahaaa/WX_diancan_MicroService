package com.atzjhydx.order.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

///**
// * 接收mq消息
// * @Auther LeeMZ
// * @Date 2021/1/31
// **/
//@Component
//@Slf4j
//public class MqReceiver {
//
//    // 1. @RabbitListener(queues = "MyQueue")
//    // 2. 自动创建队列@RabbitListener(queuesToDeclare = @Queue("MyQueue"))
//    // 3. 自动创建，Exchange和Queue绑定
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue("MyQueue"),
//            exchange = @Exchange("MyExchange")
//    ))
//    public void process(String message){
//        log.info("MqReceiver:{}",message);
//    }
//
//
//    /**
//     * 测试 exchange
//     * 水果供应商 接收消息
//     * @param message
//     */
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue("FruitQueue"),
//            exchange = @Exchange("MyOrder"),
//            key = "fruit"
//    ))
//    public void process_02(String message){
//        log.info("MqReceiver:{}",message);
//    }
//
//
//    /**
//     * 测试 exchange
//     * 饮料供应商 接收消息
//     * @param message
//     */
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue("DrinkQueue"),
//            exchange = @Exchange("MyOrder"),
//            key = "drink"
//    ))
//    public void process_03(String message){
//        log.info("MqReceiver:{}",message);
//    }
//}
