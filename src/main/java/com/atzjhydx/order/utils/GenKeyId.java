package com.atzjhydx.order.utils;

import java.util.Random;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
public class GenKeyId {

    /**
     * 生成唯一的主键
     * 格式 ： 时间戳 + 随机数
     */

    public static synchronized String genUniqueKey(){
        Random random = new Random();
        //随机生成一个数，再加上六位
        Integer number = random.nextInt(900000) + 100000;

        return System.currentTimeMillis() + String.valueOf(number);
    }
}
