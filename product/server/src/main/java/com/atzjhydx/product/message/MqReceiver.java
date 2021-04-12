package com.atzjhydx.product.message;

import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.mapper.ProductMapper;
import com.atzjhydx.product.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther LeeMZ
 * @Date 2021/4/12
 **/

@Component
@Slf4j
@Transactional
@SuppressWarnings("all")
public class MqReceiver {

    @Autowired
    private ProductMapper productMapper;

    //rabbitmq消息确认的重发机制，保证消息一定能被消费者消费，所以需要左幂等性保证，因为我不知道怎么模拟这种情况，暂时就不做了
    @RabbitListener(queuesToDeclare = @Queue("productStock"))
    public void decreaseStockInMysql(String message){

        DecreaseStockInput o = (DecreaseStockInput)JsonUtil.fromJson(message, DecreaseStockInput.class);
        log.info("从队列--{}接收到消息：{}","productInfo",o.toString());

        //扣减数据库中的库存信息,按道理说应该不会出现问题，如果出现问题，就记录到日志中吧
        int result = productMapper.decreaseStock(o.getProductQuantity(), o.getProductId());
        if (result != 1){
            log.info("productId:" + o.getProductId() + "--购买数量为：" + o.getProductQuantity()+"--数据库库存出现不足");
        }

    }
}
