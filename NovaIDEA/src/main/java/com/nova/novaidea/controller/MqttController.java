package com.nova.novaidea.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class MqttController {
    //TODO：
    @RestController
    public class TestController {
        @RequestMapping("/helloHttps")
        public String helloHttps(){
            return "hello https";
        }

    }
}
