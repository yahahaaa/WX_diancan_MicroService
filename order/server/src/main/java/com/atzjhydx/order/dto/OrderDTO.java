package com.atzjhydx.order.dto;

import com.atzjhydx.order.dataobject.OrderDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = -2158938277381960214L;

    private String orderId;

    private String buyerName;

    private String buyerPhone;

    private String buyerAddress;

    private String buyerOpenid;

    private BigDecimal orderAmount;

    //订单状态，默认为0，新下单
    private Integer orderStatus;

    //支付状态,默认为0，未支付
    private Integer payStatus;

    private List<OrderDetail> orderDetailList;
}
