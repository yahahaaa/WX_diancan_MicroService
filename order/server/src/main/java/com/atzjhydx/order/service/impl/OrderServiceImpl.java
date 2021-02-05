package com.atzjhydx.order.service.impl;

import com.atzjhydx.order.dataobject.OrderDetail;
import com.atzjhydx.order.dataobject.OrderMaster;
import com.atzjhydx.order.dto.OrderDTO;
import com.atzjhydx.order.enums.OrderStatusEnum;
import com.atzjhydx.order.enums.PayStatusEnum;
import com.atzjhydx.order.enums.ResultEnum;
import com.atzjhydx.order.exception.OrderException;
import com.atzjhydx.order.repository.OrderDetailRepository;
import com.atzjhydx.order.repository.OrderMasterRepository;
import com.atzjhydx.order.service.OrderService;
import com.atzjhydx.order.utils.GenKeyId;
import com.atzjhydx.product.client.ProductClient;
import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.common.ProductInfoOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductClient productClient;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = GenKeyId.genUniqueKey();

        // 1.查询商品信息（调用商品服务）
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());
        List<ProductInfoOutput> productInfoOutList = productClient.listForOrder(productIdList);
        // 2.计算总价（调用订单服务获得商品价格）
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            for (ProductInfoOutput productInfoOutput : productInfoOutList) {
                if (productInfoOutput.getProductId().equals(orderDetail.getProductId())){
                    //单价×数量
                    orderAmount = productInfoOutput.getProductPrice().multiply(new BigDecimal(orderDetail.getProductQuantity())).add(orderAmount);
                    BeanUtils.copyProperties(productInfoOutput,orderDetail);
                    orderDetail.setOrderId(orderId);
                    orderDetail.setDetailId(GenKeyId.genUniqueKey());
                    //订单详情入库
                    orderDetailRepository.save(orderDetail);
                }
            }
        }

        // 3.扣库存（调用订单服务扣减库存）
        List<DecreaseStockInput> cartDTOList = orderDTO.getOrderDetailList().stream()
                .map(e ->new DecreaseStockInput(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productClient.decreaseStock(cartDTOList);

        //订单入库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }

    /**
     * 完结订单，只能卖家来操作
     * @param orderId
     * @return
     */
    @Override
    @Transactional
    public OrderDTO finish(String orderId) {

        // 1. 查询订单
        Optional<OrderMaster> orderMasterOptional = orderMasterRepository.findById(orderId);
        if (!orderMasterOptional.isPresent())
            throw new OrderException(ResultEnum.ORDER_NOT_EXIST);

        // 2. 判断订单状态
        OrderMaster orderMaster = orderMasterOptional.get();
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode()))
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);

        // 3. 更改订单状态为完结
        orderMaster.setOrderStatus(OrderStatusEnum.FINISH.getCode());
        orderMasterRepository.save(orderMaster);

        // 4. 查询订单详情
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrOrderId(orderMaster.getOrderId());
        if (CollectionUtils.isEmpty(orderDetailList))
            throw new OrderException(ResultEnum.ORDER_DETAIL_NOT_EXIST);

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }
}
