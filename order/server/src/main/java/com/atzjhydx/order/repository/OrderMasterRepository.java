package com.atzjhydx.order.repository;

import com.atzjhydx.order.dataobject.OrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {
}
