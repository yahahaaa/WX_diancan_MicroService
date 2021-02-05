package com.atzjhydx.product.service.impl;

import com.atzjhydx.product.common.DecreaseStockInput;
import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.service.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void findUpAll() {
        List<ProductInfo> result = productService.findUpAll();
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void findList() {
        List<ProductInfo> list = productService.findList(Arrays.asList("157875196366160022"));
        Assert.assertNotNull(list.get(0));
    }

    @Test
    public void decreaseStock() {
        productService.decreaseStock(Arrays.asList(new DecreaseStockInput("157875196366160022",5)));
    }
}