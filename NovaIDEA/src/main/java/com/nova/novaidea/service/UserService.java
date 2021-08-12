package com.nova.novaidea.service;

import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.User;
import com.nova.novaidea.repository.InfluxdbInterface;
import com.nova.novaidea.repository.MySQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    //TODO: 保存客户id和机器id
    public JSONObject data = new JSONObject();
    @Autowired
    private MySQLInterface mySQLInterface;
    @Autowired
    private InfluxdbInterface influxdbInterface;

    //登陆
    public JSONObject isLogin(String userName, String password){
        List<User>  userList=mySQLInterface.findPerson(userName,password);
        if(userList==null||userList.size()==0)
        {
            data.put("code",101);
            data.put("id",-1);
            return data;
        }
        User user=userList.get(0);
        data.put("code",100);
        data.put("id",user.getId());
        return  data;
    }

    //获取当前用户的所有机器的id
    public List<Integer> findAllMachineId(Integer userid){
        List<Integer> machineList=mySQLInterface.findMachine(userid);
        if(machineList==null||machineList.size()==0){
            data.put("code",102);
            data.put("msg","目前还没有设备");
            return null;
        }
        data.put("code",100);
        data.put("msg","");
        return machineList;
    }

}
