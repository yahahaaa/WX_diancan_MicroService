package com.atzjhydx.order.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Entity
@Data
@DynamicUpdate
public class OrderMaster {

    @Id
    //订单id
    private String orderId;

    //买家姓名
    private String buyerName;

    //买家手机号
    private String buyerPhone;

    //买家地址
    private String buyerAddress;

    //买家微信Openid
    private String buyerOpenid;

    //订单总金额
    private BigDecimal orderAmount;

    //订单状态，默认为0，新下单
    private Integer orderStatus;

    //支付状态,默认为0，未支付
    private Integer payStatus;

    private Date createTime;

    private Date updateTime;
}
