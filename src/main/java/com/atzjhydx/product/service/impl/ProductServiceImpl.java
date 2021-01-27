package com.atzjhydx.product.service.impl;

import com.atzjhydx.enums.ResultEnum;
import com.atzjhydx.exception.ProductException;
import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.dto.CartDTO;
import com.atzjhydx.product.enums.ProductStatusEnum;
import com.atzjhydx.product.repository.ProductInfoRepository;
import com.atzjhydx.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    /**
     * 查询所有在架商品
     * @return
     */
    @Override
    public List<ProductInfo> findUpAll() {

        List<ProductInfo> productInfoList = productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
        return productInfoList;
    }

    @Override
    public List<ProductInfo> findList(List<String> productIdList) {

        List<ProductInfo> result = productInfoRepository.findByProductIdIn(productIdList);
        return result;
    }

    /**
     * 扣减库存
     * @param cartDTOList
     */
    @Override
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO : cartDTOList) {
            Optional<ProductInfo> productInfoOptional = productInfoRepository.findById(cartDTO.getProductId());
            //判断商品是否存在
            if (!productInfoOptional.isPresent()){
                throw new ProductException(ResultEnum.PRODUCT_NOT_EXIT);
            }
            //判断库存是否足够
            ProductInfo productInfo = productInfoOptional.get();
            int result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (result < 0){
                throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            //更新库存
            productInfo.setProductStock(result);
            productInfoRepository.save(productInfo);
        }
    }
}
