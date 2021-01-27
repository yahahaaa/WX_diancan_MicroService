package com.atzjhydx.product.repository;

import com.atzjhydx.product.dataobject.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import javax.ws.rs.ApplicationPath;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository repository;

    @Test
    public void findByCategoryTypeIn() {
        List<ProductCategory> result = repository.findByCategoryTypeIn(Arrays.asList(11, 22));
        Assert.assertEquals(2,result.size());
    }
}