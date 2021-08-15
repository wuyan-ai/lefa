package com.nova.novaidea.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.Machine;
import com.nova.novaidea.bean.User;
import com.nova.novaidea.repository.InfluxdbInterface;
import com.nova.novaidea.repository.MySQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    public JSONObject data = new JSONObject();
    @Autowired
    private MySQLInterface mySQLInterface;
    @Autowired
    private InfluxdbInterface influxdbInterface;

    // 获得当天0点时间
    public static String getTimesmorning(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().toLocaleString();
    }

    // 获得本周一0点时间
    public static String getTimesWeekmorning(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime().toLocaleString();
    }

    // 获得本月第一天0点时间
    public static String getTimesMonthmorning(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime().toLocaleString();
    }

    // 获得本月最后一天24点时间
    public static String getTimesMonthnight(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime().toLocaleString();
    }

    // 获得本年第一天0点时间
    public static String getCurrentYearStartTime(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR),0, 1, 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime().toLocaleString();
    }

    //将时间（格式： "2021-08-14 14:37:20.323"）转为UTC时间
    public static String localString2StringUTC(String str) {
        SimpleDateFormat utcFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//转化后UTC时间格式
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localFormater = (SimpleDateFormat) DateFormat.getDateTimeInstance();//解决Date.toLocaleString()过时
        Date date=null;
        try {
            date=localFormater.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return utcFormat.format(date);
    }

    //TODO：将UTC转换为北京时间


    //登陆
    public JSONObject isLogin(String userName, String password){
        List<User>  userList=mySQLInterface.findPerson(userName,password);
        if(userList==null||userList.size()==0)
        {
            data.put("code",101);
            data.put("msg","未查询到用户信息");
            data.put("userid",-1);
            return data;
        }
        User user=userList.get(0);
        data.put("code",100);
        data.put("msg","");
        data.put("userid",user.getId());
        return  data;
    }


    //获取当前用户的所有设备信息
    public List<Machine> findAllMachineId(Integer userid){
        List<Machine> machineList=mySQLInterface.findUserMachine(userid);
        if(machineList==null||machineList.size()==0){
            data.put("code",102);
            data.put("msg","目前还没有设备");
            return null;
        }
        data.put("code",100);
        data.put("msg","");
        return machineList;
    }


    //获取当前用户的设备列表信息  (列表界面)
    public JSONObject findAllMachineInfo(Integer userid){
        List<Machine> machineList=findAllMachineId(userid);
        if(machineList==null||machineList.size()==0) return data;
        Machine machine;
        for(int i=0;i<machineList.size();i++){      //多个设备
            machine=machineList.get(i);
            Map<String,Object> map=influxdbInterface.getMachineInfo(machine.getDeviceNum());
            if(map!=null&&map.size()!=0){
                map.put("machine_name","设备"+machine.getId());
                map.put("machineID",machine.getDeviceNum());
                data.put(machine.getId().toString(),map);
            }
        }
        return data;
    }

    //单机器展示界面数据
    /**
     *
     * @param machineNum
     * @param nowTime
     * @param flag  0:本日  1：本周 2：本月  3本年
     * @return 单机器展示界面设备显示信息
     */
    public JSONObject getOneMachineInfo(Integer machineid,String machineNum,String nowTime,int flag){
        Map<String,Object> map=influxdbInterface.getMachineInfo(machineNum);
        map.put("machine_name","设备"+machineid);
        map.put("machineID",machineNum);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date =sdf.parse(nowTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String utcStartTime;
            switch (flag){
                case 0:
                    utcStartTime=localString2StringUTC(getTimesmorning(calendar));  //当天0点对应的utc时间
                    map.put("dayOutput",influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime)));
                    break;
                case 1:
                    utcStartTime=localString2StringUTC(getTimesWeekmorning(calendar));  //本周第一天0点对应的utc时间
                    map.put("weekOutput",influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime)));
                    break;
                case 2:
                    utcStartTime=localString2StringUTC(getTimesMonthmorning(calendar));  //本月第一天0点对应的utc时间
                    map.put("monthOutput",influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime)));
                    break;
                case 3:
                    utcStartTime=localString2StringUTC(getCurrentYearStartTime(calendar));  //本年第一天0点对应的utc时间
                    map.put("yearOutput",influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime)));
                    break;
            }
        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
        }
        data=JSONObject.parseObject(JSON.toJSONString(map));
        return data;
    }


    //计算用户所有设备的产量（龙眼个数）
    //单机器展示界面数据
    /**待测试
     * @param flag  0:本日  1：本周 2：本月  3本年
     * @return  主界面 用户所有机器的产量+节省
     */
    public JSONObject calculateDayOutput(Integer userid,String nowTime,int flag){
        List<Machine> machineList=findAllMachineId(userid);
        if((int)data.get("code")!=100)
            return data;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String utcStartTime;
        Calendar calendar;
        String utcNowTime=localString2StringUTC(nowTime);   //当前时间转换为utc格式
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("msg","时区转换出错");
            return data;
        }
        Integer outputSum=0;    //用户所有设备总产量
        Double incomeSum=0.0;   //用户所有设备总收益  本日节省=15/2*（50%或100%）*100*机器一天工作时间/8
        for(Machine machine:machineList){
            switch (flag) {
                case 0:
                    utcStartTime = localString2StringUTC(getTimesmorning(calendar));  //当天0点对应的utc时间
                    outputSum += influxdbInterface.getOutput(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));
                    //incomeSum +=
                    break;
                case 1:
                    utcStartTime = localString2StringUTC(getTimesWeekmorning(calendar));  //本周第一天0点对应的utc时间
                    outputSum += influxdbInterface.getOutput(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));

                    break;
                case 2:
                    utcStartTime = localString2StringUTC(getTimesMonthmorning(calendar));  //本月第一天0点对应的utc时间
                    outputSum += influxdbInterface.getOutput(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));

                    break;
                case 3:
                    utcStartTime = localString2StringUTC(getCurrentYearStartTime(calendar));  //本年第一天0点对应的utc时间
                    outputSum += influxdbInterface.getOutput(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));

                    break;
            }
        }
        data.put("outputSum",outputSum);
        return data;
    }


    //当前北京时间向后一小时
    public String oneHourLater(String nowTime){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, 1);// 24小时制
            return calendar.getTime().toLocaleString();

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("msg","时区转换出错");
        }
       return "";
    }

    //当前北京时间向后一天
    public String oneDayLater(String nowTime){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, 24);// 24小时制
            return calendar.getTime().toLocaleString();

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("msg","时区转换出错");
        }
        return "";
    }

    //计算该用户本日（24h）每小时的产量   24
    public JSONObject dayOutputList(Integer userid,String nowTime)
    {
        List<Machine> machineList=findAllMachineId(userid);
        if((int)data.get("code")!=100)
            return data;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime;
        String endTime;
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            startTime=getTimesmorning(calendar);  //当天北京时间0点
            endTime=oneHourLater(startTime);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("code",103);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int dayOutputSum=0;
        for (int i=0;i<24;i++){
            dayOutputSum=0;
            int res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间
            if(machineList!=null&&machineList.size()!=0&&(res<=0)){
                for(Machine machine:machineList){
                    dayOutputSum += influxdbInterface.getOutput(machine.getDeviceNum(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                }
                startTime=oneHourLater(startTime);
                endTime=oneHourLater(endTime);
                list.add(dayOutputSum);
            }
           // list.add(dayOutputSum);
        }
        data.put("dayOutputList",list);
        if(list.size()==0) data.put("msg","本日产量为0");
        return data;
    }

    //计算用户本周（周一到周日）每天的收益 7
    public JSONObject weekOutputList(Integer userid,String nowTime){
        List<Machine> machineList=findAllMachineId(userid);
        if((int)data.get("code")!=100)
            return data;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime;
        String endTime;
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            startTime=getTimesmorning(calendar);  //当天北京时间0点
            endTime=oneHourLater(startTime);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("code",103);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int dayOutputSum=0;
        for (int i=0;i<7;i++){
            dayOutputSum=0;
            int res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间  大于当前时间无记录
            if(machineList!=null&&machineList.size()!=0&&(res<=0)){
                for(Machine machine:machineList){
                    dayOutputSum += influxdbInterface.getOutput(machine.getDeviceNum(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                }
                startTime=oneDayLater(startTime);
                endTime=oneDayLater(endTime);
                list.add(dayOutputSum);
            }
        }
        data.put("weekOutputList",list);
        if(list.size()==0) data.put("msg","本周产量为0");
        return data;
    }

    //计算用户本月（从本月1号开始）每天的收益   28，29，30，31？
    public JSONObject monthOutputList(Integer userid,String nowTime){
        List<Machine> machineList=findAllMachineId(userid);
        if((int)data.get("code")!=100)
            return data;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime;
        String endTime;
        String monthEndtime;//下月第一天0点  北京时间
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            startTime=getTimesmorning(calendar);  //当天北京时间0点
            endTime=oneHourLater(startTime);
            monthEndtime=getTimesMonthnight(calendar);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("code",103);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int res;
        int dayOutputSum=0;
        while (endTime.compareTo(localString2StringUTC(monthEndtime))<=0){
            dayOutputSum=0;
            res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间  大于当前时间无记录
            if(machineList!=null&&machineList.size()!=0&&(res<=0)){
                for(Machine machine:machineList){
                    dayOutputSum += influxdbInterface.getOutput(machine.getDeviceNum(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                }
                startTime=oneDayLater(startTime);
                endTime=oneDayLater(endTime);
                list.add(dayOutputSum);
            }
        }
        data.put("weekOutputList",list);
        if(list.size()==0) data.put("msg","本周产量为0");
        return data;
    }


    //计算用户本年（1-12月）每月的收益   12
    public JSONObject yearOutputList(Integer userid,String nowTime){

        return null;
    }

}
