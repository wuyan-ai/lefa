package com.nova.novaidea;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


@SpringBootApplication
public class NovaIdeaApplication {

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
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().toLocaleString();
    }



    // 获得本月最后一天24点时间
    public static String getTimesMonthnight(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime().toLocaleString();
    }

    //当前北京时间向后一小时
    public static String oneHourLater(String nowTime){
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

        }
        return "";
    }

    public static String getCurrentYearStartTime(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR),0, 1, 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime().toLocaleString();
    }

    //将UTC转换为北京时间
    public static String UTCToCST(String utcTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
        //设置时区UTC
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        //格式化，转当地时区时间
        Date after = df.parse(utcTime);
        df.applyPattern("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        return df.format(after);
    }

    //当前北京时间向后一个月
    public static String oneMonthLater(String nowTime){
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

    public static void main(String[] args) {
        SpringApplication.run(NovaIdeaApplication.class, args);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            Date date =sdf.parse("2021-08-15 01:42:43");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String temp=getTimesWeekmorning(calendar);
            System.out.println("demo0  "+getTimesWeekmorning(calendar));
            System.out.println("demo  "+UTCToCST("2021-08-15T13:41:43.90Z"));
        }catch (Exception e){
            System.out.println("calendar时间转换问题出错");
        }

        try {
            //转换为UTC时间
            System.out.println(localString2StringUTC("2021-08-14 14:37:20.323"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
