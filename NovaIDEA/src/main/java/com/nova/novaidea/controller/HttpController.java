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

    //设备列表
    @RequestMapping(value = "/machineList",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject machineList(@RequestParam Integer userid){
        return userService.findAllMachineInfo(userid);
    }

    //单个机器（折线图下方）   flag  0:本日  1：本周 2：本月  3本年
    @RequestMapping(value = "/oneMachineInfo",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject oneMachineInfo(@RequestParam Integer machineid,@RequestParam String machineNum,@RequestParam String nowTime,@RequestParam int flag){
        return userService.getOneMachineInfo(machineid,machineNum,nowTime,flag);
    }



}
