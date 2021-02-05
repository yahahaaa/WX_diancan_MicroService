package com.atzjhydx.order.message;

import com.atzjhydx.order.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @Auther LeeMZ
 * @Date 2021/1/31
 **/
//@Component
//@EnableBinding(StreamClient.class)
//@Slf4j
//public class StreamReceiver {
//    @StreamListener(StreamClient.INPUT) //监听队列
//    public void process(String message){
//        log.info("StreamReceiver: {}",message);
//    }
//
//    @StreamListener(StreamClient.OUTPUT) //监听队列
//    @SendTo(StreamClient.INPUT) // 接收消息后再向INPUT发送消息
//    public String process2(OrderDTO message){
//        log.info("StreamReceiver: {}",message);
//
//        //接收消息后再返回mq消息
//        return "received.";
//    }
//}
