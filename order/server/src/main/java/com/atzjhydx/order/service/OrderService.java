package com.atzjhydx.order.service;

import com.atzjhydx.order.dto.OrderDTO;

public interface OrderService {

    OrderDTO create(OrderDTO orderDTO);

    OrderDTO finish(String orderId);
}
