package com.atzjhydx.order.controller;

import com.atzjhydx.order.VO.ResultVO;
import com.atzjhydx.order.converter.OrderForm2OrderDTOConverter;
import com.atzjhydx.order.dto.OrderDTO;
import com.atzjhydx.order.enums.ResultEnum;
import com.atzjhydx.order.exception.OrderException;
import com.atzjhydx.order.form.OrderForm;
import com.atzjhydx.order.service.OrderService;
import com.atzjhydx.order.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
    /**
     * 1.参数校验
     * 2.查询商品信息
     * 3.计算总价
     * 4.扣库存
     * 5.订单如库
     */
    @PostMapping("/create")
    public ResultVO<Map<String,String>> create(@Valid OrderForm orderForm,
                                               BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            log.error("【创建订单】参数不正确，orderForm={}",orderForm);
            throw new OrderException(ResultEnum.PARAM_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }

        //orderForm -> orderDTO
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【购物车为空】购物车信息为空 ");
            throw new OrderException(ResultEnum.CART_IS_EMPTY);
        }

        OrderDTO result = orderService.create(orderDTO);
        Map<String,String> map = new HashMap<>();
        map.put("orderId",result.getOrderId());

        return ResultVOUtil.success(map);
    }

    @PostMapping("/finish")
    public ResultVO<OrderDTO> finish(@RequestParam("orderId")String orderId){
        OrderDTO orderDTO = orderService.finish(orderId);
        return ResultVOUtil.success(orderDTO);
    }
}
