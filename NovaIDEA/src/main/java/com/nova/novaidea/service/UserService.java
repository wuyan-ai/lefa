package com.nova.novaidea.service;

import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.Machine;

import java.util.List;
import java.util.Map;

public interface UserService {
    //登陆
    public JSONObject isLogin(String userName, String password);
    //根据机器id找机器
    public JSONObject findMachineById(Integer id);

    //获取当前用户的所有设备信息
    public JSONObject findAllMachineId(Integer userid);

    //获取当前用户的设备列表信息  (列表界面)
    public JSONObject findAllMachineInfo(List<Machine> machineList);

    //单机器页面下方
    public JSONObject getOneMachineInfo(Integer machineid,String machineNum,String nowTime,int flag);

    /**
     *折线图下方
     */
    /**
     * @param flag  0:本日  1：本周 2：本月  3本年
     * @return  主界面 用户所有机器的产量+节省
     */
    //计算所有设备的产量
    public JSONObject calculateOutputSum(List<Machine> machineList,String nowTime,int flag);
    //计算所有设备的节省
    public JSONObject calculateIncomeSum(List<Machine> machineList,String nowTime,int flag);
    //计算用户所有设备的运行时长
    public JSONObject calculateWorktimeSum(List<Machine> machineList,String nowTime,int flag);


    /**
     *折线图
     *machineList: machineid,machineNum   1  a
     * List<Map<String,Object>>
     */
    public JSONObject outputList(List<Map<String,Object>> machineList, String nowTime, int flag);

    public JSONObject upUserOutput(int userid,String nowTime,int flag);

    public JSONObject upUserWorkTime(int userid,String nowTime,int flag);

    public JSONObject upUserIncome(int userid,String nowTime,int flag);


    //计算该用户本日（24h）每小时的产量   24
    public JSONObject dayOutputList(JSONObject machineList,String nowTime);

    //计算用户本周（周一到周日）每天的收益 7
    public JSONObject weekOutputList(JSONObject machineList,String nowTime);

//    //计算用户本月（从本月1号开始）每天的收益   28，29，30，31？
//    public JSONObject monthOutputList(JSONObject machineList,String nowTime);

    //计算用户本年（1-12月）每月的收益   12
    public JSONObject yearOutputList(JSONObject machineList,String nowTime);

}
