package com.atzjhydx.product.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {

    @Update("update product_info set product_stock = product_stock - #{productQuantity} where product_id = #{productId} and product_stock - #{productQuantity} >= 0")
    int decreaseStock(@Param("productQuantity") Integer productQuantity,
                      @Param("productId")String productInd);


}
