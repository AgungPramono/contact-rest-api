package com.agung.restful.controller;

import com.agung.restful.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String hello(User user){
        return "Hello "+user.getName();
    }
}
