package com.qihoo.net.demofeign.service.impl;

import com.qihoo.net.demofeign.service.HiClient;
import org.springframework.stereotype.Component;

@Component
public class HiClientHystric implements HiClient {

    @Override
    public String sayHiFromHiClient(String name) {
        return "sorry "+name;
    }
}
