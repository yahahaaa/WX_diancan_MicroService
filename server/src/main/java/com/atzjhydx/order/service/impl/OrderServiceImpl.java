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
import com.atzjhydx.order.utils.JsonUtil;
import com.atzjhydx.product.client.ProductClient;
import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.common.ProductInfoOutput;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Service
@SuppressWarnings("all")
public class OrderServiceImpl implements OrderService {

    private static final String PRODUCT_STOCK_TEMPLATE = "product_stock_%s"; //库存剩余数量标识
    private static final String FLAG_PRODUCT_STOCK_TEMPLATE = "product_stock_invalid_%s"; //库存是否售罄标识
    private static final String PRODUCT_INFO_TEMPLATE = "product_info_%s"; //商品详情

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        //首先在redis中判断是否有商品售罄标志
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            if (stringRedisTemplate.opsForValue().get(String.format(PRODUCT_STOCK_TEMPLATE,orderDetail.getProductId())).equals("库存为0")){
                throw new OrderException(ResultEnum.PRODER_STOCK_IS_EMPTY);
            }
        }

        String orderId = GenKeyId.genUniqueKey();
        // 1.查询商品信息（调用商品服务）
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());
        List<ProductInfoOutput> productInfoOutList = productClient.listForOrder(productIdList);
        // 2.计算总价（调用订单服务，订单详情如库）
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

        // 3.扣库存（调用商品服务扣减库存）
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


    @Transactional
    public OrderDTO create_02(OrderDTO orderDTO){

        //1. 检查是否有商品售罄标识,如果有售罄标识，则直接抛出异常，库存不足
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            if(!StringUtils.isEmpty(redisTemplate.opsForValue().get(String.format("FLAG_PRODUCT_STOCK_TEMPLATE",orderDetail.getProductId())))
            && redisTemplate.opsForValue().get(String.format("FLAG_PRODUCT_STOCK_TEMPLATE",orderDetail.getProductId())).equals("true")){
                throw new OrderException(ResultEnum.PRODER_STOCK_IS_EMPTY);
            }
        }

        //2. redis 扣减库存
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            boolean result = decreaseStockInRedis(orderDetail.getProductId(), orderDetail.getProductQuantity());
            if (!result){
                throw new OrderException(ResultEnum.PRODER_STOCK_IS_EMPTY);
            }
        }

        //3. 订单详情入库
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        String orderId = GenKeyId.genUniqueKey();
        List<ProductInfoOutput> productInfoOutputList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {

            //redis中获取商品信息
            ProductInfoOutput productInfoOutput = getProductInfoById(orderDetail.getProductId());
            //向列表中添加数据，方便后面继续调用
            productInfoOutputList.add(productInfoOutput);
            //redis中查询的商品价格 乘以 商品数量
            orderAmount = productInfoOutput.getProductPrice().multiply(new BigDecimal(orderDetail.getProductQuantity())).add(orderAmount);
            BeanUtils.copyProperties(productInfoOutput,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetail.setDetailId(GenKeyId.genUniqueKey());
            //订单详情入库
            orderDetailRepository.save(orderDetail);
        }

        //4. 异步通知商品服务扣减数据库库存,这里最好实现批处理，不要for循环
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            DecreaseStockInput decreaseStockInput = new DecreaseStockInput();
            decreaseStockInput.setProductQuantity(orderDetail.getProductQuantity());
            decreaseStockInput.setProductId(orderDetail.getProductId());
            amqpTemplate.convertAndSend("productStock",JsonUtil.toJson(decreaseStockInput));
        }

        //5. 订单入库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());

        orderMasterRepository.save(orderMaster);

        return orderDTO;
    }

    //通过redis中的increase函数保证扣减库存的原子性
    public boolean decreaseStockInRedis(String id, Integer amount){

        long result = redisTemplate.opsForValue().increment(String.format(PRODUCT_STOCK_TEMPLATE,id),amount.intValue() * -1);
        if (result > 0){
            return true;
        }else if (result == 0){
            //刚好卖完
            //设置库存售罄标识
            redisTemplate.opsForValue().set(String.format(FLAG_PRODUCT_STOCK_TEMPLATE,id),"true");
            return true;
        }else{
            //回补库存
            redisTemplate.opsForValue().increment(String.format(PRODUCT_STOCK_TEMPLATE,id),amount.intValue());
            return false;
        }
    }

    /**
     * 通过商品id去redis中查询商品信息
     * 如果redis中每个该商品，则通过商品服务去数据库查询
     * @param id
     * @return
     */
    public ProductInfoOutput getProductInfoById(String id){
        ProductInfoOutput productInfo = (ProductInfoOutput)redisTemplate.opsForValue().get(String.format(PRODUCT_INFO_TEMPLATE, id));
        if (productInfo == null){
            List<ProductInfoOutput> productInfoOutputs = productClient.listForOrder(Arrays.asList(id));
            if (productInfoOutputs.size() == 0){
                //说明没有该商品，直接抛出异常
                throw new OrderException(ResultEnum.ORDER_DETAIL_NOT_EXIST);
            }
            productInfo = productInfoOutputs.get(0);
            redisTemplate.opsForValue().set(String.format(PRODUCT_INFO_TEMPLATE,productInfo.getProductId()),productInfoOutputs.get(0));
            redisTemplate.expire(String.format(PRODUCT_INFO_TEMPLATE,productInfo.getProductId()),10, TimeUnit.MINUTES);
        }

        return productInfo;
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
