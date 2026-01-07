package com.jinzhougang.innvotion.ykcxv1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tippler")
public class Tippler {
    @RequestMapping("/index")
    public String returnTippler(){
        return "index";
    }
}
