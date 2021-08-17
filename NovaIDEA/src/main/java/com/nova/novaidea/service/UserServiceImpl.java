package com.nova.novaidea.service;

import com.alibaba.fastjson.JSONObject;
import com.nova.novaidea.bean.Machine;
import com.nova.novaidea.bean.User;
import com.nova.novaidea.repository.InfluxdbInterface;
import com.nova.novaidea.repository.MySQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService{

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
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);

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
    //当前北京时间向后一小时
    public String oneHourLater(String nowTime){
        JSONObject data = new JSONObject();
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
        JSONObject data = new JSONObject();
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

    //当前北京时间向后一个月
    public String oneMonthLater(String nowTime){
        JSONObject data = new JSONObject();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONDAY, 1);// 24小时制
            return calendar.getTime().toLocaleString();

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("msg","时区转换出错");
        }
        return "";
    }



    //登陆
    public JSONObject isLogin(String userName, String password){
        JSONObject data = new JSONObject();
        List<User>  userList=mySQLInterface.findPerson(userName,password);
        if(userList==null||userList.size()==0)
        {
            data.put("code",1001);
            data.put("msg","未查询到用户信息");
            data.put("userid",-1);
            return data;
        }
        User user=userList.get(0);
        data.put("code",1000);
        data.put("msg","");
        data.put("userid",user.getId());
        return  data;
    }

    //根据机器id找机器
    public JSONObject findMachineById(Integer id){
        Machine machine=mySQLInterface.findById(id);
        JSONObject data=new JSONObject();
        if(machine==null){
            data.put("code",3000);
            data.put("msg","未查询到设备");
            data.put("data","");
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",machine);
        return data;
    }

    //获取当前用户的设备列表信息  (列表界面)
    public JSONObject findAllMachineInfo(List<Machine> machineList){
        Machine machine;
        Map<String,Object> map;
        JSONObject result=new JSONObject();
        JSONObject data=new JSONObject();
        List<Map<String,Object>> resultList=new LinkedList<>();
        for(int i=0;i<machineList.size();i++){      //多个设备
            map=new HashMap<>();
            machine=machineList.get(i);
            result=influxdbInterface.getStateAndUpdateTime(machine.getDeviceNum());
            if(result==null){
                map.put("machineUpdateTime","暂无");
                map.put("machineStatus","暂无");
                map.put("machineWorkTime",0);
                map.put("machine_name","设备"+machine.getId());
                map.put("machineID",machine.getDeviceNum());
                resultList.add(map);
                continue;
            }
            if((int)result.get("code")!=1000) return result;
            map=(Map<String,Object>)result.get("data");
            if(map==null||map.size()==0){
                map=new HashMap<>();
                map.put("machineUpdateTime","暂无");
                map.put("machineStatus","暂无");
                map.put("machineWorkTime",0);
            }
            map.put("machine_name","设备"+machine.getId());
            map.put("machineID",machine.getDeviceNum());
            resultList.add(map);
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",resultList);
        return data;
    }

    //获取当前用户的所有设备信息
    public JSONObject findAllMachineId(Integer userid){
        JSONObject data = new JSONObject();
        List<Machine> machineList=mySQLInterface.findUserMachine(userid);
        if(machineList==null||machineList.size()==0){
            data.put("code",3001);
            data.put("msg","目前还没有设备");
            return data;
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",machineList);
        return data;
    }


    //单机器展示界面数据
    /**
     * @param machineNum
     * @param nowTime
     * @param flag  0:本日  1：本周 2：本月  3本年
     * @return 单机器展示界面设备显示信息
     */
    public JSONObject getOneMachineInfo(Integer machineid,String machineNum,String nowTime,int flag){
        JSONObject data=new JSONObject();
        if(flag<0||flag>3) {
            data.put("code",3002);
            data.put("msg","0:本日  1：本周 2：本月  3本年 flag值超界");
        }
        data=influxdbInterface.getStateAndUpdateTime(machineNum);
        if((int)data.get("code")!=1000) return data;
        Map<String,Object> map=(Map<String,Object>)data.get("data");   //查
        data= new JSONObject();
        map.put("machine_name","设备"+machineid);
        map.put("machineID",machineNum);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject temp;
        try{
            Date date =sdf.parse(nowTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String utcStartTime = "";
            switch (flag){
                case 0:
                    utcStartTime=localString2StringUTC(getTimesmorning(calendar));  //当天0点对应的utc时间
                    break;
                case 1:
                    utcStartTime=localString2StringUTC(getTimesWeekmorning(calendar));  //本周第一天0点对应的utc时间
                    break;
                case 2:
                    utcStartTime=localString2StringUTC(getTimesMonthmorning(calendar));  //本月第一天0点对应的utc时间
                    //map.put("output",influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime)));
                    break;
                case 3:
                    utcStartTime=localString2StringUTC(getCurrentYearStartTime(calendar));  //本年第一天0点对应的utc时间
                    //map.put("output",influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime)));
                    break;
            }
            temp=influxdbInterface.getOutput(machineNum,utcStartTime,localString2StringUTC(nowTime));
            if((int)temp.get("code")!=1000) return temp;
            map.put("output",temp.get("data"));
            temp=influxdbInterface.getWorkTime(machineNum,utcStartTime,localString2StringUTC(nowTime));
            if((int)temp.get("code")!=1000) return temp;
            map.put("machineWorkTime",temp.get("data"));
        }catch (Exception e){
            data.put("code",3002);
            data.put("msg","calendar时间转换问题出错");
            return data;
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",map);
        return data;
    }


    //计算所有设备的产量（龙眼个数）
    /**待测试
     * @param flag  0:本日  1：本周 2：本月  3本年
     * @return  主界面 用户所有机器的产量
     */
    public JSONObject calculateOutputSum(List<Machine> machineList, String nowTime,int flag){
        JSONObject data = new JSONObject();
        if(flag<0||flag>3) {
            data.put("code",3001);
            data.put("msg","0:本日  1：本周 2：本月  3本年 flag值超界");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String utcStartTime = "";
        Calendar calendar;
        String utcNowTime=localString2StringUTC(nowTime);   //当前时间转换为utc格式
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);

        }catch (Exception e){
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        Integer outputSum=0;    //用户所有设备总产量
        Double incomeSum=0.0;   //用户所有设备总收益  本日节省=15/2*（50%或100%）*100*机器一天工作时间/8
        JSONObject temp=new JSONObject();
        for(Machine machine:machineList){
            switch (flag) {
                case 0:
                    utcStartTime = localString2StringUTC(getTimesmorning(calendar));  //当天0点对应的utc时间

                    break;
                case 1:
                    utcStartTime = localString2StringUTC(getTimesWeekmorning(calendar));  //本周第一天0点对应的utc时间

                    break;
                case 2:
                    utcStartTime = localString2StringUTC(getTimesMonthmorning(calendar));  //本月第一天0点对应的utc时间

                    break;
                case 3:
                    utcStartTime = localString2StringUTC(getCurrentYearStartTime(calendar));  //本年第一天0点对应的utc时间

                    break;
            }
            temp=influxdbInterface.getOutput(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));
            if((int)temp.get("code")!=1000) return temp;
            outputSum += Double.valueOf(temp.get("data").toString()).intValue();
            //incomeSum +=
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",outputSum);
        return data;
    }

    public JSONObject calculateIncomeSum(List<Machine> machineList,String nowTime,int flag){
        JSONObject data = new JSONObject();
        if(flag<0||flag>3) {
            data.put("code",3001);
            data.put("msg","0:本日  1：本周 2：本月  3本年 flag值超界");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String utcStartTime = "";
        Calendar calendar;
        String utcNowTime=localString2StringUTC(nowTime);   //当前时间转换为utc格式
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);

        }catch (Exception e){
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        Double incomeSum=0.0;   //用户所有设备总收益  本日节省=15/2*（50%或100%）*100*机器一天工作时间/8
        JSONObject temp=new JSONObject();
        for(Machine machine:machineList){
            switch (flag) {
                case 0:
                    utcStartTime = localString2StringUTC(getTimesmorning(calendar));  //当天0点对应的utc时间

                    break;
                case 1:
                    utcStartTime = localString2StringUTC(getTimesWeekmorning(calendar));  //本周第一天0点对应的utc时间

                    break;
                case 2:
                    utcStartTime = localString2StringUTC(getTimesMonthmorning(calendar));  //本月第一天0点对应的utc时间

                    break;
                case 3:
                    utcStartTime = localString2StringUTC(getCurrentYearStartTime(calendar));  //本年第一天0点对应的utc时间

                    break;
            }
            temp=influxdbInterface.getWorkTime(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));
            if((int)temp.get("code")!=1000) return temp;
            //TODO:节省计算公式待定
            incomeSum +=15/2*100*Double.valueOf(temp.get("data").toString())/8;
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",incomeSum);
        return data;
    }

    //计算用户所有设备的运行时长
    public JSONObject calculateWorktimeSum(List<Machine> machineList,String nowTime,int flag){
        JSONObject data = new JSONObject();
        if(flag<0||flag>3) {
            data.put("code",3001);
            data.put("msg","0:本日  1：本周 2：本月  3本年 flag值超界");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String utcStartTime = "";
        Calendar calendar;
        String utcNowTime=localString2StringUTC(nowTime);   //当前时间转换为utc格式
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);

        }catch (Exception e){
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        int worktime=0;   //用户所有设备总收益  本日节省=15/2*（50%或100%）*100*机器一天工作时间/8
        JSONObject temp=new JSONObject();
        for(Machine machine:machineList){
            switch (flag) {
                case 0:
                    utcStartTime = localString2StringUTC(getTimesmorning(calendar));  //当天0点对应的utc时间

                    break;
                case 1:
                    utcStartTime = localString2StringUTC(getTimesWeekmorning(calendar));  //本周第一天0点对应的utc时间

                    break;
                case 2:
                    utcStartTime = localString2StringUTC(getTimesMonthmorning(calendar));  //本月第一天0点对应的utc时间

                    break;
                case 3:
                    utcStartTime = localString2StringUTC(getCurrentYearStartTime(calendar));  //本年第一天0点对应的utc时间

                    break;
            }
            temp=influxdbInterface.getWorkTime(machine.getDeviceNum(), utcStartTime, localString2StringUTC(nowTime));
            if((int)temp.get("code")!=1000) return temp;
            worktime +=Double.valueOf(temp.get("data").toString()).intValue();
        }
        data.put("code",1000);
        data.put("msg","");
        data.put("data",worktime);
        return data;
    }


    /**
     * 折线图
     * @param machineList
     * @param nowTime
     * @return
     */
    //计算该设备本日（24h）每小时的产量   24
    public JSONObject dayOutputList(JSONObject machineList,String nowTime)
    {
        JSONObject data = new JSONObject();
        List<Map<String,Object>> machineIdAndNum=(List<Map<String,Object>>)machineList;
        if(machineIdAndNum==null||machineIdAndNum.size()!=0){
            data.put("code",1000);
            data.put("msg","设备数为0");
        }
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
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int dayOutputSum=0;
        JSONObject temp;
        for (int i=0;i<24;i++){
            dayOutputSum=0;
            int res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间
            if(res<=0){
                for(Map<String,Object> machine:machineIdAndNum){
                    temp= influxdbInterface.getOutput(machine.get("machineNum").toString(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                    if((int)temp.get("code")!=1000) return temp;
                    dayOutputSum += Double.valueOf(temp.get("data").toString()).intValue();
                }
                startTime=oneHourLater(startTime);
                endTime=oneHourLater(endTime);
                list.add(dayOutputSum);
            }
           // list.add(dayOutputSum);
        }
        data.put("code",1000);
        data.put("data",list);
        if(list.size()==0) data.put("msg","本日产量为0");
        return data;
    }

    //计算设备本周（周一到周日）每天的产量 7
    public JSONObject weekOutputList(JSONObject machineList,String nowTime){
        JSONObject data = new JSONObject();
        List<Map<String,Object>> machineIdAndNum=(List<Map<String,Object>>)machineList;
        if(machineIdAndNum==null||machineIdAndNum.size()!=0){
            data.put("code",1000);
            data.put("msg","设备数为0");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime;
        String endTime;
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            startTime=getTimesWeekmorning(calendar);  //本周第一天北京时间0点
            endTime=oneHourLater(startTime);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int dayOutputSum=0;
        JSONObject temp;
        for (int i=0;i<7;i++){
            dayOutputSum=0;
            int res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间  大于当前时间无记录
            if(res<=0){
                for(Map<String,Object> machine:machineIdAndNum){
                    temp = influxdbInterface.getOutput(machine.get("machineNum").toString(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                    if((int)temp.get("code")!=1000) return temp;
                    dayOutputSum += Double.valueOf(temp.get("data").toString()).intValue();
                }
                startTime=oneDayLater(startTime);
                endTime=oneDayLater(endTime);
                list.add(dayOutputSum);
            }
        }
        data.put("code",1000);
        data.put("data",list);
        if(list.size()==0) data.put("msg","本周产量为0");
        return data;
    }

    //计算用户本月（从本月1号开始）每天的收益   28，29，30，31？
    public JSONObject monthOutputList(JSONObject machineList,String nowTime){
        JSONObject data = new JSONObject();
        List<Map<String,Object>> machineIdAndNum=(List<Map<String,Object>>)machineList;
        if((int)machineList.get("code")!=1000)
            return machineList;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime;
        String endTime;
        String monthEndtime;//下月第一天0点  北京时间
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            startTime=getTimesMonthmorning(calendar);  //本月第一天北京时间0点
            endTime=oneHourLater(startTime);
            monthEndtime=getTimesMonthnight(calendar);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int res;
        int dayOutputSum=0;
        JSONObject temp;
        while (endTime.compareTo(localString2StringUTC(monthEndtime))<=0){
            dayOutputSum=0;
            res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间  大于当前时间无记录
            if(res<=0){
                for(Map<String,Object> machine:machineIdAndNum){
                    temp = influxdbInterface.getOutput(machine.get("machineNum").toString(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                    if((int)temp.get("code")!=1000) return temp;
                    dayOutputSum += Double.valueOf(temp.get("data").toString()).intValue();
                }
                startTime=oneDayLater(startTime);
                endTime=oneDayLater(endTime);
                list.add(dayOutputSum);
            }
        }
        data.put("code",1000);
        data.put("data",list);
        if(list.size()==0) data.put("msg","本月产量为0");
        return data;
    }


    //计算用户本年（1-12月）每月的收益   12
    public JSONObject yearOutputList(JSONObject machineList,String nowTime){
        JSONObject data = new JSONObject();
        List<Map<String,Object>> machineIdAndNum=(List<Map<String,Object>>)machineList;
        if(machineIdAndNum==null||machineIdAndNum.size()!=0){
            data.put("code",1000);
            data.put("msg","设备数为0");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime;
        String endTime;
        Calendar calendar;
        try {
            Date date = sdf.parse(nowTime);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            startTime=getCurrentYearStartTime(calendar);  //本年第一天北京时间0点
            endTime=oneMonthLater(startTime);

        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
            data.put("code",3003);
            data.put("msg","时区转换出错");
            return data;
        }
        List<Integer> list=new LinkedList<>();
        int dayOutputSum=0;
        JSONObject temp;
        for (int i=0;i<12;i++){
            dayOutputSum=0;
            int res=startTime.compareTo(nowTime); //res<=0表示startTime<=当前时间  大于当前时间无记录
            if(res<=0){
                for(Map<String,Object> machine:machineIdAndNum){
                    temp = influxdbInterface.getOutput(machine.get("machineNum").toString(), localString2StringUTC(startTime), localString2StringUTC(endTime));
                    if((int)temp.get("code")!=1000) return temp;
                    dayOutputSum += Double.valueOf(temp.get("data").toString()).intValue();
                }
                startTime=oneMonthLater(startTime);
                endTime=oneMonthLater(endTime);
                list.add(dayOutputSum);
            }
        }
        data.put("code",1000);
        data.put("data",list);
        if(list.size()==0) data.put("msg","本年产量为0");
        return data;
    }

}
