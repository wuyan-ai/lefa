package com.nova.novaidea.service;

import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.Machine;

import java.util.List;

public interface UserService {
    //登陆
    public JSONObject isLogin(String userName, String password);

    //获取当前用户的所有设备信息
    public List<Machine> findAllMachineId(Integer userid);

    //获取当前用户的设备列表信息  （机器列表页面）
    public JSONObject findAllMachineInfo(Integer userid);

    //单机器页面下方
    public JSONObject getOneMachineInfo(Integer machineid,String machineNum,String nowTime,int flag);

    /**
     *折线图下方
     */
    //单机器展示界面数据
    /**
     * @param flag  0:本日  1：本周 2：本月  3本年
     * @return  主界面 用户所有机器的产量+节省
     */
    public JSONObject calculateDayOutput(Integer userid,String nowTime,int flag);

    //计算用户所有设备的本日运行时长（待定）


    /**
     *折线图
     *
     */
    //计算该用户本日（24h）每小时的产量   24
    public List<Integer> dayOutputList(Integer userid,String startTime);

    //计算用户本周（周一到周日）每天的收益 7
    public List<Integer> weekOutputList(Integer userid);

    //计算用户本月（从本月1号开始）每天的收益   28，29，30，31？
    public List<Integer> monthOutputList(Integer userid);

    //计算用户本年（1-12月）每月的收益   12
    public List<Integer> yearOutputList(Integer userid);

}
