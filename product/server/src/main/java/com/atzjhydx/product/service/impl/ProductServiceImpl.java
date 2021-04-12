package com.atzjhydx.product.service.impl;

import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.enums.ResultEnum;
import com.atzjhydx.product.exception.ProductException;
import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.enums.ProductStatusEnum;
import com.atzjhydx.product.mapper.ProductMapper;
import com.atzjhydx.product.repository.ProductInfoRepository;
import com.atzjhydx.product.service.ProductService;
import com.atzjhydx.product.utils.JsonUtil;
import org.springframework.amqp.core.AmqpTemplate;
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
@SuppressWarnings("all")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private ProductMapper productMapper;

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


//    @Override
//    @Transactional //如果这里不加注解，下面方法中的decreaseStockProcess方法对象和通过AOP动态代理生成的decreaseStockProcess不是一个方法对象，会导致事务不起作用
//    public void decreaseStock(List<DecreaseStockInput> cartDTOList) {
//
//        //操作数据库
//        List<ProductInfo> productInfoList = decreaseStockProcess(cartDTOList);
//
//        //int a = 10 / 0;
//
//        //发送mq消息
//        //将productInfo转为productInfoOutput
//        List<ProductInfoOutput> results = productInfoList.stream().map(e -> {
//            ProductInfoOutput productInfoOutput = new ProductInfoOutput();
//            BeanUtils.copyProperties(e, productInfoOutput);
//            return productInfoOutput;
//        }).collect(Collectors.toList());
//        amqpTemplate.convertAndSend("productInfo", JsonUtil.toJson(results));//直连模式，直接发送到productInfo队列中
//    }

    /**
     * 商品服务收到减库存请求后，首先根据商品Id在数据库查询该商品是否存在，
     * 如果商品存在，通过乐观锁扣减库存，若扣减库存，结束方法
     * 商品不存在直接抛出异常
     * 如果库存不足，查询当前最新的库存，若库存为0，就通过rabbitmq向order服务发送消息，告知库存不足
     * @param cartDTOList
     */
    @Transactional
    public void decreaseStock(List<DecreaseStockInput> cartDTOList){ //扣减库存成功，返回True，失败返回false
        //List<ProductInfo> results = new ArrayList<>();
        for (DecreaseStockInput cartDTO : cartDTOList) {
            //判断商品是否存在可以通过redis判断
            Optional<ProductInfo> productInfoOptional = productInfoRepository.findById(cartDTO.getProductId());
            //判断商品是否存在
            if (!productInfoOptional.isPresent()) {
                throw new ProductException(ResultEnum.PRODUCT_NOT_EXIT);
            }
//            ProductInfo productInfo = productInfoOptional.get();
//            int result = productInfo.getProductStock() - cartDTO.getProductQuantity();
//            if (result < 0) {
//                throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);
//            }
//            //更新库存
//            productInfo.setProductStock(result);
//            results.add(productInfo);
//            productInfoRepository.save(productInfo);

            int result = productMapper.decreaseStock(cartDTO.getProductQuantity(), cartDTO.getProductId());
            //若更改成功的话，会返回更改成功的数据数量，也就是1
            if (result != 1){
                Optional<ProductInfo> curProductInfo = productInfoRepository.findById(cartDTO.getProductId());
                //最新的库存信息
                int curStockInfo = curProductInfo.get().getProductStock();
                //如果已经没有库存，就发送消息到order服务
                if (curStockInfo == 0){
                    //更改失败，向order服务发送消息，将当前商品最新的库存信息通知redis
                    DecreaseStockInput stockInput = new DecreaseStockInput();
                    stockInput.setProductId(cartDTO.getProductId());
                    stockInput.setProductQuantity(curStockInfo);
                    amqpTemplate.convertAndSend("productInfo",JsonUtil.toJson(stockInput));
                }
                //发送消息后抛出异常
                throw new ProductException(ResultEnum.PRODUCT_STOCK_ERROR);
            }
        }
    }
}
