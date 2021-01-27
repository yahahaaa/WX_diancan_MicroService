package com.atzjhydx.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther LeeMZ
 * @Date 2021/1/26
 **/
@RestController
public class ServerController {

    @GetMapping("/msg")
    public String msg(){
        return "this is product msg";
    }
}
