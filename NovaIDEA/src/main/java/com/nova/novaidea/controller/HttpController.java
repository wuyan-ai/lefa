package com.nova.novaidea.controller;


import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.Machine;
import com.nova.novaidea.service.MachineService;
import com.nova.novaidea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

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
    /**
     * userid
     */
    @RequestMapping(value = "/machineList",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject machineList(@RequestBody JSONObject jsonObject){
        JSONObject result=userService.findAllMachineId((int)jsonObject.get("userid"));
        if((int)result.get("code")!=1000) return result;
        List<Machine> machineList=(List<Machine>)result.get("data");
        return userService.findAllMachineInfo(machineList);
    }

    /**单个机器（折线图下方）
     * machineid
     * machineNum
     * nowTime
     * flag  0:本日  1：本周 2：本月  3本年
     */
    @RequestMapping(value = "/oneMachineInfo",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject oneMachineInfo(@RequestBody JSONObject machineInfo){

        return userService.getOneMachineInfo((int)machineInfo.get("machineid"),machineInfo.get("machineNum").toString(),machineInfo.get("nowTime").toString(),(int)machineInfo.get("flag"));
    }


    /**
     * 获取用户设备实时产量  主界面左上角
     * userid
     * nowTime
     * flag  0:本日  1：本周 2：本月  3本年
     */
    @RequestMapping(value = "/nowUserOutputSum",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject nowUserOutputSum(@RequestBody JSONObject jsonObject){
        JSONObject result=userService.findAllMachineId((int)jsonObject.get("userid"));
        if((int)result.get("code")!=1000) return result;
        List<Machine> machineList=(List<Machine>)result.get("data");
        return  userService.calculateOutputSum(machineList,jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
    }
    /**
     * 获取单个设备实时产量  单个设备界面左上角
     * machineid
     * nowTime
     * flag  0:本日  1：本周 2：本月  3本年
     */
    @RequestMapping(value = "/nowMachineOutputSum",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject nowMachineOutputSum(@RequestBody JSONObject jsonObject){
        JSONObject result=userService.findMachineById((int)jsonObject.get("machineid"));
        if((int)result.get("code")!=1000) return result;
        List<Machine> machineList=new LinkedList<>();
        machineList.add((Machine) result.get("data"));
        return  userService.calculateOutputSum(machineList,jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
    }



}
