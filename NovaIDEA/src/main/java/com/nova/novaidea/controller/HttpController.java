package com.nova.novaidea.controller;


import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.Machine;
import com.nova.novaidea.service.MachineService;
import com.nova.novaidea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取折线图上的点
     * machineIdAndNum List<Map<String,Object>> machineNum machineid
     * nowTime
     * flag  0:本日  1：本周 2：本月  3本年
     */
    @RequestMapping(value = "/outputList",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject outputList(@RequestBody JSONObject jsonObject){
        return  userService.outputList((List<Map<String,Object>>) jsonObject.get("machineIdAndNum"),jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
    }


    /**
     * 主界面下方的点
     * userid
     * nowTime
     * flag  0:本日  1：本周 2：本月  3本年
     */
    @RequestMapping(value = "/upUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject upUserInfo(@RequestBody JSONObject jsonObject){
        JSONObject data=new JSONObject();
        Map<String,Object> map=new HashMap<>();
        JSONObject result=userService.findAllMachineId((int)jsonObject.get("userid"));
        if((int)result.get("code")!=1000) return result;
        List<Machine> machineList=(List<Machine>)result.get("data");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(3);//保留3位小数
        nf.setRoundingMode(RoundingMode.HALF_UP); //四舍五入
        NumberFormat format = NumberFormat.getPercentInstance();//转百分数
        format.setMaximumFractionDigits(1);

        //总产量
        JSONObject temp=userService.calculateOutputSum(machineList,jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
        if((int)temp.get("code")!=1000) return temp;
        map.put("outputSum",Double.valueOf(temp.get("data").toString()).intValue());

        temp=userService.upUserOutput((int)jsonObject.get("userid"),jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
        if((int)temp.get("code")!=1000) return temp;
        map.put("upUserOutput",format.format(Double.valueOf(nf.format(Double.valueOf(temp.get("data").toString())))));


        //总运行时长
        temp=userService.calculateWorktimeSum(machineList,jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
        if((int)temp.get("code")!=1000) return temp;
        map.put("worktimeSum",Double.valueOf(temp.get("data").toString()));

        temp=userService.upUserWorkTime((int)jsonObject.get("userid"),jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
        if((int)temp.get("code")!=1000) return temp;
        map.put("upUserWorkTime",format.format(Double.valueOf(nf.format(Double.valueOf(temp.get("data").toString())))));

        //总节省
        temp=userService.calculateIncomeSum(machineList,jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
        if((int)temp.get("code")!=1000) return temp;
        map.put("incomeSum",Double.valueOf(temp.get("data").toString()).intValue());

        temp=userService.upUserIncome((int)jsonObject.get("userid"),jsonObject.get("nowTime").toString(),(int)jsonObject.get("flag"));
        if((int)temp.get("code")!=1000) return temp;
        map.put("upUserIncome",format.format(Double.valueOf(nf.format(Double.valueOf(temp.get("data").toString())))));

        data.put("code",1000);
        data.put("msg","");
        data.put("data",map);
        return data;
    }


}
