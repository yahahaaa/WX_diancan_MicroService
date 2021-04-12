package com.atzjhydx.order.message;

import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.common.ProductInfoOutput;
import com.atzjhydx.order.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther LeeMZ
 * @Date 2021/1/31
 **/
@Component
@Slf4j
@Transactional
public class ProductInfoReceiver {

    private static final String PRODUCT_STOCK_TEMPLATE = "product_stock_%s";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


//    @RabbitListener(queuesToDeclare = @Queue("productInfo"))
//    public void process(String message){
//        //message -> ProductInfoOutput
//        List<ProductInfoOutput> productInfoOutputList = (List<ProductInfoOutput>) JsonUtil.fromJson(message,
//                new TypeReference<List<ProductInfoOutput>>() {
//
//        });
//        log.info("从队列--{}接收到消息：{}","productInfo",productInfoOutputList);
//
//        //接收到消息后存储到redis中
//        for (ProductInfoOutput productInfoOutput : productInfoOutputList) {
//            stringRedisTemplate.opsForValue().set(String.format(PRODUCT_STOCK_TEMPLATE,productInfoOutput.getProductId()),String.valueOf(productInfoOutput.getProductStock()));
//        }
//    }

    @RabbitListener(queuesToDeclare = @Queue("productInfo"))
    public void process(String message){
        //message -> ProductInfoOutput
        DecreaseStockInput o  = (DecreaseStockInput)JsonUtil.fromJson(message, DecreaseStockInput.class);
        log.info("从队列--{}接收到消息：{}","productInfo",o.toString());

        //接收到消息后存储到redis中
        if (o.getProductQuantity() == 0){
            stringRedisTemplate.opsForValue().set(String.format(PRODUCT_STOCK_TEMPLATE,o.getProductId()),"库存为0");
        }
    }
}
