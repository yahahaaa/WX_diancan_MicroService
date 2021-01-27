package com.atzjhydx.order.repository;

import com.atzjhydx.order.dataobject.OrderMaster;
import com.atzjhydx.order.enums.OrderStatusEnum;
import com.atzjhydx.order.enums.PayStatusEnum;
import org.aspectj.weaver.ast.Or;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMasterRepositoryTest {

    @Autowired
    private OrderMasterRepository repository;

    @Test
    public void testSave(){
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setOrderId("123456");
        orderMaster.setBuyerName("王老菊");
        orderMaster.setBuyerAddress("上海");
        orderMaster.setBuyerOpenid("abc123");
        orderMaster.setBuyerPhone("10082008820");
        orderMaster.setOrderAmount(new BigDecimal(12));
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        OrderMaster result = repository.save(orderMaster);
        Assert.assertNotNull(result);
    }

}