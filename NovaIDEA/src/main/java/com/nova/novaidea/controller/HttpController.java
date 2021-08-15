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
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject login(@RequestBody JSONObject account ){
        return userService.isLogin(account.get("username").toString(),account.get("password").toString());
    }

    //设备列表
    @RequestMapping(value = "/machineList",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject machineList(@RequestBody JSONObject userid){
        return userService.findAllMachineInfo((int)userid.get("userid"));
    }

    //单个机器（折线图下方）   flag  0:本日  1：本周 2：本月  3本年
    @RequestMapping(value = "/oneMachineInfo",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject oneMachineInfo(@RequestBody JSONObject machineInfo){
        return userService.getOneMachineInfo((int)machineInfo.get("machineid"),machineInfo.get("machineNum").toString(),machineInfo.get("nowTime").toString(),(int)machineInfo.get("flag"));
    }



}
