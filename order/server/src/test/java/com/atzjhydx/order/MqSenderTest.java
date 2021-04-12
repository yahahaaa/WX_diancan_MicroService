package com.atzjhydx.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    private static final String PRODUCT_STOCK_TEMPLATE = "product_stock_%s"; //库存剩余数量标识

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

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

    //发布秒杀商品信息到redis中
    @Test
    public void promoProductInfo(){
        redisTemplate.opsForValue().set(String.format(PRODUCT_STOCK_TEMPLATE,"157875196366160022"),10000);
    }

}
