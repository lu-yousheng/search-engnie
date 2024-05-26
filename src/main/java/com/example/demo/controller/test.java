package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class test {
    @RequestMapping("/hello")
    public static String print(){
        return "hello Security!!";
    }
    @RequestMapping("/ee")
    public static String prints(){return "no hello";}

}
