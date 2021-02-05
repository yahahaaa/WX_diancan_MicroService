package com.atzjhydx.product.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @Auther LeeMZ
 * @Date 2021/1/31
 **/
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 转换为json字符串
     * @return
     */
    public static String toJson(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object fromJson(String string,Class classType){
        try {
            return objectMapper.readValue(string,classType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
