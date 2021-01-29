package com.atzjhydx.product.client;

import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.common.ProductInfoOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Auther LeeMZ
 * @Date 2021/1/28
 **/
//微服务调用应用名：product商品服务,打成jar包供订单服务使用
@FeignClient(name = "product")
public interface ProductClient {

    @PostMapping("/product/listForOrder")
    List<ProductInfoOutput> listForOrder(@RequestBody List<String> productIdList);

    @PostMapping("/product/decreaseStock")
    void decreaseStock(@RequestBody List<DecreaseStockInput> decreaseStockInputList);
}
