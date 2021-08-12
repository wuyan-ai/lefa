package com.nova.novaidea.controller;

import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.service.MachineService;
import com.nova.novaidea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class HttpController {
    //TODO：Http
    /**
     json:
     code:
     msg:
     */
    @Autowired
    private UserService userService;
    @Autowired
    private MachineService machineService;

    //登录
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject login(@RequestParam String userName, @RequestParam String password){
        return userService.isLogin(userName,password);
    }
}
