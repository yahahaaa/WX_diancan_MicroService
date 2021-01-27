package com.atzjhydx.product.service.impl;

import com.atzjhydx.product.dataobject.ProductCategory;
import com.atzjhydx.product.repository.ProductCategoryRepository;
import com.atzjhydx.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList) {

        List<ProductCategory> productCategoryList = productCategoryRepository.findByCategoryTypeIn(categoryTypeList);
        return productCategoryList;
    }
}
