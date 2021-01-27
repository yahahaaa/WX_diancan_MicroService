package com.atzjhydx.product.service.impl;

import com.atzjhydx.product.ProductApplicationTests;
import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.dto.CartDTO;
import com.atzjhydx.product.service.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest{

    @Resource
    private ProductService productService;

    @Test
    public void findUpAll() {
        List<ProductInfo> result = productService.findUpAll();
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void decreaseStock() {
        CartDTO cartDTO = new CartDTO("157875196366160022",2);
        List<CartDTO> list = new ArrayList<>();
        list.add(cartDTO);
        productService.decreaseStock(list);
    }
}