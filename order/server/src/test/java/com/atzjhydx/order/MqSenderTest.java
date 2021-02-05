package com.atzjhydx.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * 测试发送mq消息
 * @Auther LeeMZ
 * @Date 2021/1/31
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MqSenderTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void send(){
        amqpTemplate.convertAndSend("MyQueue","now " + new Date());
    }

    @Test
    public void sendOrderFruit(){
        amqpTemplate.convertAndSend("MyOrder","fruit","now " + new Date());
    }

    @Test
    public void sendOrderDrink(){
        amqpTemplate.convertAndSend("MyOrder","drink","now " + new Date());
    }
}
