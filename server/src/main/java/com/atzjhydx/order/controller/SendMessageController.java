package com.atzjhydx.order.controller;

//import com.atzjhydx.order.dto.OrderDTO;
//import com.atzjhydx.order.message.StreamClient;
//import org.aspectj.weaver.ast.Or;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.math.BigDecimal;
//import java.util.Date;

///**
// * @Auther LeeMZ
// * @Date 2021/1/31
// **/
//@RestController
//public class SendMessageController {
//
//    @Autowired
//    private StreamClient streamClient;
//
//    @GetMapping("/sendMessage")
//    public void process(){
//        OrderDTO orderDTO = new OrderDTO();
//        orderDTO.setBuyerPhone("134543433");
//        orderDTO.setBuyerOpenid("abc123");
//        orderDTO.setOrderId("222222");
//        orderDTO.setOrderAmount(new BigDecimal(2));
//        streamClient.output().send(MessageBuilder.withPayload(orderDTO).build());
//    }
//}
