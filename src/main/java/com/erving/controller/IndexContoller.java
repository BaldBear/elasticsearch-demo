package com.erving.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author erving
 * @date 2020/6/15
 **/
@Controller
public class IndexContoller {
    @RequestMapping({"/", "/index"})
    public String index(){
        return "index";
    }
}
