package com.qihoo.net.demofeign.controller;

import com.qihoo.net.demofeign.service.HiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HiController {

    @Autowired
    HiClient hiClient;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return hiClient.sayHiFromHiClient( name );
    }
}