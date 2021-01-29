package com.atzjhydx.product.repository;

import com.atzjhydx.product.dataobject.ProductInfo;
import com.atzjhydx.product.enums.ProductStatusEnum;
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
public class ProductInfoRepositoryTest {

    @Autowired
    private ProductInfoRepository repository;

    @Test
    public void findByProductStatus() {
        List<ProductInfo> result = repository.findByProductStatus(ProductStatusEnum.UP.getCode());
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void findByProductIdIn() {
        List<ProductInfo> result = repository.findByProductIdIn(Arrays.asList("157875196366160022"));
        Assert.assertTrue(result.size() > 0);
    }
}