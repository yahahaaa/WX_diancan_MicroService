package com.atzjhydx.product.service.impl;

import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.common.ProductInfoOutput;
import com.atzjhydx.product.enums.ResultEnum;
import com.atzjhydx.product.exception.ProductException;
import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.enums.ProductStatusEnum;
import com.atzjhydx.product.repository.ProductInfoRepository;
import com.atzjhydx.product.service.ProductService;
import com.atzjhydx.product.utils.JsonUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

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


    @Override
    @Transactional //如果这里不加注解，下面方法中的decreaseStockProcess方法对象和通过AOP动态代理生成的decreaseStockProcess不是一个方法对象，会导致事务不起作用
    public void decreaseStock(List<DecreaseStockInput> cartDTOList) {

        //操作数据库
        List<ProductInfo> productInfoList = decreaseStockProcess(cartDTOList);

        //int a = 10 / 0;

        //发送mq消息
        //将productInfo转为productInfoOutput
        List<ProductInfoOutput> results = productInfoList.stream().map(e -> {
            ProductInfoOutput productInfoOutput = new ProductInfoOutput();
            BeanUtils.copyProperties(e, productInfoOutput);
            return productInfoOutput;
        }).collect(Collectors.toList());
        amqpTemplate.convertAndSend("productInfo", JsonUtil.toJson(results));
    }

    /**
     * 扣减库存,高并发下先查库存然后再减库存有可能会发生超卖问题
     * 将数据库扣减库存提取出来，因为这一段如果出错，数据库内容一起回滚，和mq的逻辑分开
     * @param cartDTOList
     */
    @Transactional
    public List<ProductInfo> decreaseStockProcess(List<DecreaseStockInput> cartDTOList){
        List<ProductInfo> results = new ArrayList<>();
        for (DecreaseStockInput cartDTO : cartDTOList) {
            Optional<ProductInfo> productInfoOptional = productInfoRepository.findById(cartDTO.getProductId());
            //判断商品是否存在
            if (!productInfoOptional.isPresent()) {
                throw new ProductException(ResultEnum.PRODUCT_NOT_EXIT);
            }
            //判断库存是否足够
            ProductInfo productInfo = productInfoOptional.get();
            int result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if (result < 0) {
                throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            //更新库存
            productInfo.setProductStock(result);
            results.add(productInfo);
            productInfoRepository.save(productInfo);
        }
        return results;
    }
}
