package com.atzjhydx.product.service;

import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.dto.CartDTO;

import java.util.List;

public interface ProductService {

    //查询所有在架商品
    List<ProductInfo> findUpAll();

    //根据商品Id查询商品列表
    List<ProductInfo> findList(List<String> productIdList);

    //扣减库存
    void decreaseStock(List<CartDTO> cartDTOList);

}
